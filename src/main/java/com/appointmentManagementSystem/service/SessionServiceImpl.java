package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.enums.EnumVisitorStatus;
import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntitySessionPatient;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.repository.InvitationRepository;
import com.appointmentManagementSystem.repository.SessionPatientRepository;
import com.appointmentManagementSystem.repository.SessionRepository;
import com.appointmentManagementSystem.enums.EnumSessionQueryPurpose;
import com.appointmentManagementSystem.repository.UserRepository;
import com.appointmentManagementSystem.model.*;

import com.appointmentManagementSystem.payload.AddSesssionPayload;
import com.appointmentManagementSystem.repository.*;
import com.appointmentManagementSystem.util.Constant;

import com.appointmentManagementSystem.util.ImageUtils;
import com.appointmentManagementSystem.util.QrCodeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.appointmentManagementSystem.enums.EnumVisitorStatus.*;


@Service
public class SessionServiceImpl implements SessionService{

    SessionRepository sessionRepository;

    SessionPatientRepository sessionPatientRepository;

    EntityManager em;

    EmailService emailService;

    UserService userService;


    public static AuthenticationManager authenticationManager;


    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    public Dictionary<String, EnumSessionQueryPurpose> dict = new Hashtable<>();

    @Autowired
    public SessionServiceImpl(UserService userService, AuthenticationManager authenticationManager, SessionRepository sessionRepository, SessionPatientRepository sessionPatientRepository, EntityManager em, EmailService emailService) {
        this.sessionRepository=sessionRepository;
        this.sessionPatientRepository=sessionPatientRepository;
        this.em=em;
        this.userService=userService;
        this.emailService=emailService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public List<EntitySession> findAll(String strPurpose) {

        dict.put("management",EnumSessionQueryPurpose.MANAGEMENT);
        dict.put("report",EnumSessionQueryPurpose.REPORT);
        dict.put("calendar",EnumSessionQueryPurpose.CALENDAR);
        EnumSessionQueryPurpose purpose = dict.get(strPurpose);

        if(purpose == null)
            return null;

        List<EntitySession> allSessions = new ArrayList<>();
        Date currentDate = new Date();
        if(purpose == EnumSessionQueryPurpose.MANAGEMENT || purpose == EnumSessionQueryPurpose.CALENDAR){ // if it's for management or calendar page, set max date to too far, if it's for reports page set date to one day before today so only show finished exh.
            allSessions= sessionRepository.findAllByDeleted(false);
        }else if( purpose == EnumSessionQueryPurpose.REPORT){
            currentDate = DateUtils.addDays(currentDate,-1);
            allSessions = sessionRepository.findAll(currentDate);
        }


       return allSessions;
   }

    @Override
    public List<EntitySession> findSessionByUserId(Long userId) {

        Optional<EntityUser> u = userRepository.findById(userId);

        if(!u.isPresent()){
            return null;
        }else{
            return sessionRepository.findByUserId(userId);
        }

    }


    @Override
    public EntitySession saveSession(AddSesssionPayload sesssionPayload)  {
        System.out.println(sesssionPayload.toString());
        if(sesssionPayload.getSessionID() == -1L){
            EntitySession newSession = new EntitySession();
            newSession.setDeleted(false);
            newSession.setSessionCount(1);
            newSession.setDate(sesssionPayload.getSessionDate());
            newSession.setStatus(sesssionPayload.getStatus());

            Optional<EntityUser> u1 = userService.getUserById(sesssionPayload.getPsychologistID());
            if(u1.isPresent()){
                newSession.setPsychologist_user(u1.get());
            }else{
                return null;
            }

            EntitySession session = sessionRepository.save(newSession);


            Optional<EntityUser> u = userService.getUserById(sesssionPayload.getPatientID());
            if(u.isPresent()){
                EntitySessionPatient sp = EntitySessionPatient.builder().patientSession(session).user(u.get()).status(WAITING).invitation(null).deleted(false).build();
                sp = sessionPatientRepository.save(sp);
                session.setPatient_user(sp);
            }else{
                return null;
            }

            session = sessionRepository.save(session);

            return session;
        }else{
            Optional<EntitySession> s = sessionRepository.findByIdAndDeleted(sesssionPayload.getSessionID(),false);
            if (s.isPresent()){
                EntitySession session = s.get();
                session.setStatus(sesssionPayload.getStatus());

                Optional<EntitySessionPatient> p = sessionPatientRepository.findById(sesssionPayload.getPatientID());
                if (p.isPresent()){
                    EntitySessionPatient patient = p.get();
                    EntityUser user = patient.getUser();
                    EntitySessionPatient sp = EntitySessionPatient.builder().patientSession(session).user(user).status(WAITING).invitation(null).deleted(false).build();
                    sp = sessionPatientRepository.save(sp);
                    session.setPatient_user(sp);

                }else{
                    return null;
                }

                Optional<EntityUser> u1 = userService.getUserById(sesssionPayload.getPsychologistID());
                if(u1.isPresent()){
                    session.setPsychologist_user(u1.get());
                }else{
                    return null;
                }

                return sessionRepository.save(session);
            }else{
                return null;
            }
        }
    }

    private void checkChangesForMail(AddSesssionPayload payload, EntitySession exhibitionOld) throws MessagingException, IOException, URISyntaxException {

        if (exhibitionOld.getDate().compareTo(payload.getSessionDate())!=0){
            String updateMessage = "Tarih/Saat bilgisi güncellenmiştir.";
            sendExhibitionUpdateMail(payload, exhibitionOld, updateMessage);
        }

    }

    private void sendExhibitionUpdateMail(AddSesssionPayload payload, EntitySession exhibitionOld, String updateMessage) throws MessagingException, IOException, URISyntaxException {
        List<EntitySessionPatient> exhibitionVisitors = sessionPatientRepository.findBySession_Id(payload.getSessionID());

        Instant instant = payload.getSessionDate().toInstant();
        ZoneId z = ZoneId.of ("Europe/Istanbul");
        ZonedDateTime istanbul = instant.atZone(z);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedDate = istanbul.format(formatter);
        String iCalString = "";
        for(EntitySessionPatient visitor : exhibitionVisitors){
            Optional<Long> inv = invitationRepository.findInvitationByUserIdAndSessionId(visitor.getUser().getId(),payload.getSessionID());

            // create new calendar if date is updated
            if(updateMessage.contains("Tarih")){
                iCalString = emailService.createCal(visitor.getUser(),payload.getSessionDate());
            }
            try{
                Thread.sleep(500);
            }catch (Exception e){};
            Map<String, Object> mailVariablesMapping = new HashMap<String, Object>();
            mailVariablesMapping.put("name", visitor.getUser().getFullname());

            /*if(updateMessage.contains("Tarih")){
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(visitor.getUser().getEmail(), visitor.getUser().getPassword()));
                String jwt = jwtUtils.generateJwtToken(authentication,payload.getDate());
                String newLink = Constant.HOST_NAME+"/home?token=" + jwt;
                mailVariablesMapping.put("link", newLink);

                userRepository.updateGuestInvitationLinkByUserID(visitor.getUser().getId(), newLink);
                invitationRepository.UpdateLink(visitor.getId(),newLink);
            }else{
                Optional<EntitySessionPatientInvitation> invitation = invitationRepository.findById(inv.get());
                EntitySessionPatientInvitation userInvitation = invitation.get();
                mailVariablesMapping.put("link", userInvitation.getLink());
            }*/

            mailVariablesMapping.put("link", "https://google.com");
            mailVariablesMapping.put("sessionDate",formattedDate);
            mailVariablesMapping.put("updateMessage",updateMessage);
            mailVariablesMapping.put("sign", "Java Virtual Lab - Exhibition Management");


            try {
                emailService.sendEmailHTMLForUpdate(visitor.getUser().getEmail(),"PSK. Gamze Bayan - Seans Güncellendi",mailVariablesMapping, iCalString);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }


    private void sendInformationMailToUser(EntityUser entityUser, EntitySession entityExhibition) throws IOException, MessagingException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", entityUser.getFullname());
        model.put("link", Constant.HOST_NAME+"/login");
        model.put("paylasimLinki", Constant.HOST_NAME+"/register?id="+entityExhibition.getId());


        Instant instant = entityExhibition.getDate().toInstant();
        ZoneId z = ZoneId.of ("Europe/Istanbul");
        ZonedDateTime istanbul = instant.atZone(z);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedString = istanbul.format(formatter);
        model.put("sessionDate",formattedString);

        try {
            BufferedImage encode = QrCodeUtil.encode(Constant.HOST_NAME+"/login", null, false);
            byte[] jpgs = ImageUtils.toByteArray(encode, "jpg");
            model.put("qrCode",jpgs);
        }catch (Exception e ){
        }


        model.put("sign", "Java Virtual Lab - Exhibition Management");

        try {
            String str = emailService.createCal(entityUser,entityExhibition.getDate());
            emailService.sendEmailHTML(entityUser.getEmail(),"PSK. Gamze Bayan Seans",null,str,model);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    @Transactional
    public void deleteSessionById(long id) throws Exception {
        List<EntitySessionPatient> ExhibitionVisitors = sessionPatientRepository.findBySession_Id( id);

        for (EntitySessionPatient sessionPatient : ExhibitionVisitors) {
            Optional<Long> inv = invitationRepository.findInvitationByUserIdAndSessionId(sessionPatient.getUser().getId(), id);
            if(inv.isPresent()){
                Long invitationIdOfUser = inv.get();
                invitationRepository.updateDeleted(invitationIdOfUser);
            }

            sessionPatientRepository.updateDeleted(sessionPatient.getId());
        }
        sessionRepository.updateDeleted(id);
    }


}
