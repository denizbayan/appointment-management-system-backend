package com.appointmentManagementSystem.service;


import com.appointmentManagementSystem.model.EntitySessionPatientInvitation;
import com.appointmentManagementSystem.repository.InvitationRepository;
import com.appointmentManagementSystem.repository.SessionPatientRepository;
import com.appointmentManagementSystem.enums.EnumInvitationStatus;
import com.appointmentManagementSystem.enums.EnumUserStatus;
import com.appointmentManagementSystem.enums.EnumVisitorStatus;
import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntitySessionPatient;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.UserSessionInfo;
import com.appointmentManagementSystem.payload.UserInvitations;
import com.appointmentManagementSystem.repository.UserRepository;
import com.appointmentManagementSystem.security.JwtUtils;
import com.appointmentManagementSystem.util.Constant;
import com.appointmentManagementSystem.util.ImageUtils;
import com.appointmentManagementSystem.util.QrCodeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public static AuthenticationManager authenticationManager;

    InvitationRepository invitationRepository;

    EmailService emailService;

    UserRepository userRepository;

    public static JwtUtils jwtUtils;

    PasswordEncoder encoder;

    @Autowired
    SessionPatientRepository sessionPatientRepository;


    @Autowired
    public InvitationServiceImpl(PasswordEncoder encoder, InvitationRepository invitationRepository, EmailService emailService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }

    @Override
    public EntitySessionPatientInvitation createInvitation(Long exhibitionVisitorId) throws Exception {

        Optional<EntitySessionPatient> ev =  sessionPatientRepository.findById(exhibitionVisitorId);

        if(ev.isPresent()){
            EntitySessionPatient sessionPatient = ev.get();
            boolean invitationExists = checkIfExistsSession(sessionPatient);
            if(invitationExists){
                throw new Exception("Katılımıcının aktif davetiyesi vardır.Yeni davetiye oluşturamazsınız.");
            }
            EntitySessionPatientInvitation invitation = new EntitySessionPatientInvitation();
            EntityUser visitorUser = sessionPatient.getUser();

            String link = generateLink(visitorUser,sessionPatient.getPatientSession().getDate());
            String invitationCode = generateInvitationCode();
            byte[] imageQr = generateQR(link);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            String sessionUserName = userPrincipal.getUsername();

            Optional<EntityUser> byUsername = userRepository.findByUsername(sessionUserName);
            EntityUser sessionUser = byUsername.get();
            invitation.setSessionPatient(sessionPatient);
            invitation.setStatus(EnumInvitationStatus.UYGUN);
            invitation.setCreatorUser(sessionUser);
            invitation.setInvitationCode(invitationCode);
            invitation.setCreateDate(new Date());
            invitation.setLink(link);
            invitation.setQrCode(imageQr);
            invitation.setDeleted(false);
            try {
                invitation = invitationRepository.save(invitation);
                visitorUser.setStatus(EnumUserStatus.Aktif);
                visitorUser.setPassword(encoder.encode(visitorUser.getFullname() + visitorUser.getEmail()));
                visitorUser.setGuestInvitationLink(link);
                userRepository.save(visitorUser);
            } catch (Exception e) {

                throw new Exception("Davetiye oluşturulamadı.Detay: " + e.getMessage());
            }
            sendMail(invitation);
            return invitation;
        }
        throw new Exception("Katılımcı bulunamadı. Katılımcıyı etkinlikten silip tekrar ekleyiniz.");
    }

    @Override
    public Iterable<EntitySessionPatientInvitation> findAll() {

        checkInvitationTokenStatus();
        return invitationRepository.findAll();
    }

    @Transactional
    public void setTokenExpireStatus(EntitySessionPatientInvitation invitation) {

        Date expireDate = new Date((invitation.getSessionPatient().getPatientSession().getDate()).getTime() + jwtExpirationMs);

        if(expireDate.before(new Date())){
                EntitySessionPatient visitorUser = invitation.getSessionPatient();
                visitorUser.getUser().setGuestInvitationLink("");
                userRepository.save(visitorUser.getUser());
                invitation.setStatus(EnumInvitationStatus.ZAMAN_ASIMI);
                invitationRepository.save(invitation);
                passiveVisitor(visitorUser.getUser().getId());
        }

    }

    @Override
    public void deleteInvitationById(long id) {

        Optional<EntitySessionPatientInvitation> invitation = invitationRepository.findById(id);
        EntitySessionPatientInvitation inv = invitation.get();
        passiveVisitor(inv.getSessionPatient().getUser().getId());
        inv.setDeleted(true);
        invitationRepository.save(inv);
    }

    @Override
    @Transactional
    public Iterable<UserSessionInfo> getInvitationsByUser(long userId) {
        Date date = new Date();
        Date now = new Date();
        date = DateUtils.addHours(date, -24);// exhibitions will disappear 1 day after exhibition started
        now = DateUtils.addHours(now, +9999);//  exhibition will be visible from the date it was created. So look forward too much find all invitations
        Iterable<UserSessionInfo> invitationByUserId = invitationRepository.findSessionByUserId(userId,date, EnumVisitorStatus.ACCEPTED,now);
        return invitationByUserId;
    }

    @Override
    public void setUsedToken(String token) {
        // EntityExhibitionVisitorInvitation byLinkLike = invitationRepository.findDistinctByLinkLike(token);
       // byLinkLike.setStatus(EnumInvitationStatus.USED);
    }

    /****************************** HELPER METHODS ******************************/

    private void passiveVisitor(long userId) {
        Iterable<UserInvitations> invitations =  invitationRepository.findInvitationByUserId(userId);
        boolean hasValidInvitation = false;
        for(UserInvitations inv : invitations){
            Optional<EntitySessionPatientInvitation> invitation = invitationRepository.findById(inv.getSessionPatientInvitationId());
            if(invitation.isPresent()){
                EntitySessionPatientInvitation evi = invitation.get();
                if(evi.getStatus() == EnumInvitationStatus.UYGUN){
                    hasValidInvitation = true;
                }
            }
        }
        if(!hasValidInvitation){
            Optional<EntityUser> u = userRepository.findById(userId);
            if(u.isPresent()){
                EntityUser user = u.get();
                user.setStatus(EnumUserStatus.Pasif);
                user.setGuestInvitationLink("");
                userRepository.save(user);
            }
        }
    }

    private void sendMail(EntitySessionPatientInvitation saved) throws MessagingException, IOException {
        EntityUser user = saved.getSessionPatient().getUser();
        EntitySession session = saved.getSessionPatient().getPatientSession();
        /*StringBuilder base64 = new StringBuilder("data:image/png;base64,");
        base64.append(Base64.getEncoder().encodeToString(saved.getQrCode()));*/
/*
        emailService.sendSimpleMessage(user.getEmail(), "PSK. Gamze Bayan Seansına Davetlisiniz.Etkinliğe katılmak için QR kodu kullanabilirsiniz.<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>");
*/
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", user.getFullname());
        model.put("link", saved.getLink());
        model.put("paylasimLinki", Constant.HOST_NAME+"/register?id="+session.getId());

        Instant instant = session.getDate().toInstant();
        ZoneId z = ZoneId.of ("Europe/Istanbul");
        ZonedDateTime istanbul = instant.atZone(z);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedString = istanbul.format(formatter);
        model.put("sessionDate",formattedString);

        String encodedString = Base64.getEncoder().encodeToString(saved.getQrCode());
        model.put("barkod", encodedString);


        model.put("sign", "PSK. Gamze Bayan Seans Yönetim Sistemi");
        try {
            String str = emailService.createCal(user,session.getDate());
            emailService.sendEmailHTML(user.getEmail(),"PSK. Gamze Bayan Seans",saved.getQrCode(),str,model);
        } catch (URISyntaxException e) {
            throw  new RuntimeException("Mail gönderim hatası. Bazı kullanıcılara mail gitmemiş olabilir. Lütfen Etkinliği tekrar güncelleyiniz.");
        }
    }

    private byte[] generateQR(String link) throws Exception {
        BufferedImage encode = QrCodeUtil.encode(link, null, false);
        byte[] jpgs = ImageUtils.toByteArray(encode, "jpg");
        return jpgs;
    }

    private String generateInvitationCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private String generateLink(EntityUser user, Date sessionDate) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getFullname(), user.getFullname() + user.getEmail()));
        String jwt = jwtUtils.generateJwtToken(authentication,sessionDate);
        return Constant.HOST_NAME+"/home?token=" + jwt;
    }

    private void checkInvitationTokenStatus() {
        invitationRepository.findAll().forEach(invitation -> setTokenExpireStatus(invitation));
    }

    private boolean checkIfExistsSession(EntitySessionPatient patient) {
        return invitationRepository.existsBySessionPatientAndStatus(patient,EnumInvitationStatus.UYGUN);
    }


}
