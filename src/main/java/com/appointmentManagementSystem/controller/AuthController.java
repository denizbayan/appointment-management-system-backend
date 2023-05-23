package com.appointmentManagementSystem.controller;


import com.appointmentManagementSystem.model.EntitySessionPatientInvitation;
import com.appointmentManagementSystem.payload.*;
import com.appointmentManagementSystem.repository.InvitationRepository;
import com.appointmentManagementSystem.security.JwtUtils;
import com.appointmentManagementSystem.service.InvitationService;
import com.appointmentManagementSystem.service.PasswordResetService;
import com.appointmentManagementSystem.enums.EnumUserStatus;
import com.appointmentManagementSystem.model.EntityRole;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.enums.EnumRole;
import com.appointmentManagementSystem.service.UserDetailsImpl;
import com.appointmentManagementSystem.repository.RoleRepository;
import com.appointmentManagementSystem.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    InvitationService invitationService;

    @Autowired
    InvitationRepository invitationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired

    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        Optional<EntityUser> u =  userRepository.findByEmail(email);
        if(!u.isPresent()){
            return ResponseEntity.badRequest().body("Email kayıtlı değil");
        }
        EntityUser entityUser =u.get();

        if(entityUser.getBanLoginUntil() !=null){
            if(new Date().before(entityUser.getBanLoginUntil())){
                Instant instant = entityUser.getBanLoginUntil().toInstant();
                ZoneId z = ZoneId.of ("Europe/Istanbul");
                ZonedDateTime istanbul = instant.atZone(z);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
                String formattedString = istanbul.format(formatter);
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Hesabınız " + formattedString+" tarihine kadar geçici olarak bloke edilmiştir."));
            }
        }

        if(encoder.matches(loginRequest.getPassword(), entityUser.getPassword())){
            entityUser.setPasswordFailCount(0);
            userRepository.save(entityUser);
        }
        else {
            entityUser.setPasswordFailCount(entityUser.getPasswordFailCount()+1);
            entityUser = userRepository.save(entityUser);
            if(entityUser.getPasswordFailCount() == 5){
                entityUser.setBanLoginUntil(DateUtils.addMinutes(new Date(),30));
                entityUser.setPasswordFailCount(0);
                userRepository.save(entityUser);
            }
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(entityUser.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication, new Date());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Bu mail adresi kullanılmaktadır"));
        }

        String numericRE = ".*\\d.*";
        String lowerCaseRE = ".*[a-z].*";
        String upperCaseRE = ".*[A-Z].*";
        String specialCharRE = ".*\\W.*";
        String whiteSpaceRE = ".*\\s.*";
        String msg = "";

        if(signUpRequest.getPassword().contains(signUpRequest.getFullname())){
            msg +="Şifre kullanıcı adını içeremez.";
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(msg));
        }
        if(signUpRequest.getPassword().matches(whiteSpaceRE)){
            msg +="Şifre boşluk içeremez.";
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(msg));
        }
        if(signUpRequest.getPassword().length() <8 ){
            msg +="Şifre en az 8 karakter olmalıdır.";
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(msg));
        }

        if(!signUpRequest.getPassword().matches(numericRE)){
            msg +="- en az 1 adet sayı.";
        }
        if(!signUpRequest.getPassword().matches(lowerCaseRE)){
            msg +="- en az 1 adet küçük karakter.";
        }
        if(!signUpRequest.getPassword().matches(upperCaseRE)){
            msg +="- en az 1 adet büyük karakter.";
        }
        if(!signUpRequest.getPassword().matches(specialCharRE)){
            msg += "- en az 1 adet özel karakter (@#/*& gibi).";
        }


        if(!msg.equals("")){
            msg = "Şifre;."+msg+"içermelidir"; // put dot because angular splits with .
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(msg));
        }

        // Create new user's account
        EntityUser user = new EntityUser(signUpRequest.getFullname(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getGender(),
                signUpRequest.getCellPhone(),
                signUpRequest.getBirthdate()
                );

        user.setDeleted(false);

        Set<String> strRoles = signUpRequest.getRole();
        Set<EntityRole> roles = new HashSet<>();

        if (strRoles == null) {
            EntityRole userRole = roleRepository.findByRoleName(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Hata: Rol bulunamadı."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        EntityRole adminRole = roleRepository.findByRoleName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Hata: Rol Bulunamadı."));
                        roles.add(adminRole);

                        break;
                    default:
                        EntityRole userRole = roleRepository.findByRoleName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Hata: Rol Bulunamadı."));
                        roles.add(userRole);
                }
            });
        }
        user.setRole(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Kullanıcı Kaydı Başarılı!"));
    }

    @PostMapping("/validateToken/{sessionId}")
    public ResponseEntity<?> validateToken(@PathVariable Long sessionId,@RequestBody String token){
        boolean b = jwtUtils.validateJwtToken(token);
        if(b)
        {
            String userNameFromJwtToken = jwtUtils.getUserNameFromJwtToken(token);
            Optional<EntityUser> byUsername = userRepository.findByUsername(userNameFromJwtToken);

            if(!byUsername.isPresent()){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Geçersiz Token.Katılımcı bulunamadı."));
            }
            EntityUser entityUser = byUsername.get();

            if(sessionId == -1){ // invitation token validation
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userNameFromJwtToken, entityUser.getFullname()+entityUser.getEmail()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication, new Date());
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());

                Optional<EntitySessionPatientInvitation> byToken = invitationRepository.findByInvitationLink(token);
                if(!byToken.isPresent() ){
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Daha güncel bir davetiyeniz bulunmaktadır. Lütfen yeni davetiye ile tekrar deneyiniz."));
                }
                if(EnumUserStatus.Pasif == entityUser.getStatus())
                {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Davetiye süreniz dolmuş,Etkinlik sona ermiş veya Yönetici tarafından silinmiş olabilir.Lütfen davetiye sağlayıcınızla iletişime geçiniz."));

                }
                invitationService.setUsedToken(token);

                return ResponseEntity.ok(new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
            }

        }
        else{
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Davetiye süreniz dolmuş veya etkinlik sona ermiş.Lütfen davetiye sağlayıcınızla iletişime geçiniz."));

        }
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Geçersiz Token"));
    }

    @PostMapping("/sendSecurityCode")
    public ResponseEntity<?> sendSecurityCode(@Valid @RequestBody String email) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map
                    = mapper.readValue(email, new TypeReference<Map<String,Object>>(){});

            passwordResetService.sendSecurityCode(map.get("email").toString());
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.ok(new MessageResponse("Güvenlik kodu başarıyla gönderildi."));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            logger.info("resetPassword"+resetPasswordRequest.toString());

            passwordResetService.resetPassword(resetPasswordRequest);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.ok(new MessageResponse("Şifreniz başarıyla güncellenmiştir!"));
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            passwordResetService.ChangePassword(changePasswordRequest);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("Şifreniz başarıyla güncellenmiştir!"));
    }

}
