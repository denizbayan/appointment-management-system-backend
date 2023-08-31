package com.appointmentManagementSystem.service;


import com.appointmentManagementSystem.model.EntityRole;
import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.UserInvitations;
import com.appointmentManagementSystem.payload.UserSessions;
import com.appointmentManagementSystem.repository.*;
import com.appointmentManagementSystem.model.*;
import com.appointmentManagementSystem.enums.EnumRole;
import com.appointmentManagementSystem.payload.SignupRequest;
import com.appointmentManagementSystem.repository.*;
import com.appointmentManagementSystem.util.CustomException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private SessionPatientRepository sessionPatientRepository;

    @Autowired
    private SessionRepository sessionRepository;


    @Autowired
    public UserServiceImpl(PasswordEncoder encoder,UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.roleRepository=roleRepository;
        this.encoder =encoder;
    }

    @Override
    public EntityUser addUser(SignupRequest request) {


        Set<EntityRole> roles = new HashSet<>();

        if (request.getRole() == null) {
            EntityRole userRole = roleRepository.findByRoleName(EnumRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            request.getRole().forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        EntityRole adminRole = roleRepository.findByRoleName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;

                    default:
                        EntityRole userRole = roleRepository.findByRoleName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        String password1 = encoder.encode(request.getFullname()+request.getEmail());
        EntityUser newUser = new EntityUser(request.getFullname(),
                request.getEmail(),
                encoder.encode(password1),
                request.getGender(),
                request.getCellPhone(),
                request.getBirthdate()
        );
        newUser.setDeleted(false);
        newUser.setRole(roles);
        if(request.getCellPhone()!=null)
            newUser.setCellPhone(request.getCellPhone());
       return userRepository.save(newUser);

    }

    @Override
    public Iterable<EntityUser> findAll() {
        return  userRepository.findAll();
    }

    @Override
    public Optional<EntityUser> getUserById(Long id) {
        return userRepository.findById(id);

    }

    @Override
    public String getAvatar() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
                if (userPrincipal != null) {
                    String sessionUserName = userPrincipal.getUsername();
                    if (sessionUserName != null) {
                        return userRepository.findByUsername(sessionUserName).get().getAvatar();
                    }

                }
            }
            return "";
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Boolean setAvatar(String avatar) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
                if (userPrincipal != null) {
                    String sessionUserName = userPrincipal.getUsername();
                    if (sessionUserName != null) {
                        Optional <EntityUser> u = userRepository.findByUsername(sessionUserName);
                        if(u.isPresent()){
                           EntityUser user = u.get();
                            user.setAvatar(avatar);
                            System.out.println(avatar);
                            userRepository.save(user);
                            return true;
                        }
                    }

                }
            }


        }catch (Exception e) {
            e.printStackTrace();

        }

        return false;
    }


    @Override
    public void deleteUserById(long id) throws CustomException {

        Optional<EntityUser> userToBeDeleted = userRepository.findById(id);
        EntityUser user = userToBeDeleted.get();

        user.setDeleted (true);
        user.setGuestInvitationLink(null);

        user.setEmailBeforeDeleted(user.getEmail());
        user.setEmail(user.getEmail()+ UUID.randomUUID());
        userRepository.save(user);

        //check if user has invitation, if exhibition which invitation is created for is active then delete invitation otherwise update as deleted
        Date startDate =DateUtils.addHours(new Date(), -24);// sessions will disappear 1 day after exhibition started
        Iterable<UserInvitations> invitationsOfUser = invitationRepository.findInvitationByUserId(id);
        invitationsOfUser.forEach(invitation -> {
            if(invitation.getSessionDate().after(startDate)){
                invitationRepository.deleteById(invitation.getSessionPatientInvitationId());
            }else{
                invitationRepository.Updatedeleted(invitation.getSessionPatientInvitationId());
            }
        });

        //check if user is in exhibitions, if exhibition is active then delete exhibition visitor id otherwise update as deleted
        Iterable<UserSessions> sessions = sessionPatientRepository.findSessionsOfUser(id);
        sessions.forEach(session -> {
            if(session.getSessionDate().after(startDate)){
                Optional<EntitySession> ex= sessionRepository.findById(session.getSessionId());
                EntitySession exSession = ex.get();

                sessionRepository.save(exSession);

                sessionPatientRepository.deleteById(session.getSessionPatientId());
            }else{
                sessionPatientRepository.Updatedeleted(session.getSessionPatientId());
            }
        });
    }

    @Override
    public boolean existsByEmail(String mail) {
        return userRepository.existsByEmail(mail);
    }

    @Override
    public EntityUser updateUser(SignupRequest request,Long id) throws CustomException {

        Optional<EntityUser> u = userRepository.findById(id);
        if(!u.isPresent()){
            return null;
        }
        EntityUser user = u.get();

        if(request.getEmail()!=null)
            user.setEmail(request.getEmail());
        if(request.getBirthdate()!=null)
            user.setBirthdate(request.getBirthdate());
        if(request.getGender()!=null)
            user.setGender(request.getGender());
        if(request.getFullname()!=null)
            user.setFullname(request.getFullname());

        if(request.getCellPhone()!=null)
            user.setCellPhone(request.getCellPhone());
        return userRepository.save(user);

    }

}
