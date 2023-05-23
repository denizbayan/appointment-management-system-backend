package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.model.EntitySessionPatientInvitation;
import com.appointmentManagementSystem.payload.MessageResponse;
import com.appointmentManagementSystem.payload.UserSessionInfo;
import com.appointmentManagementSystem.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invitation")
public class InvitationController {

    @Autowired
    InvitationService invitationService;

    @PostMapping("/createInvitation/{visitorId}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> createInvitation(@PathVariable Long visitorId){
        try{
            EntitySessionPatientInvitation invitation = invitationService.createInvitation(visitorId);
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));

        }
    }

    @PostMapping("/getInvitations")
    @PreAuthorize(" hasRole('ADMIN')")
    public Iterable<EntitySessionPatientInvitation> getInvitations() {
        return invitationService.findAll();
    }


    @PostMapping("/deleteInvitationById/{id}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteInvitationById(@PathVariable("id") long id) {
        try {
            invitationService.deleteInvitationById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getInvitationsByUser/{userId}")
    @PreAuthorize("hasRole('USER')")
    public Iterable<UserSessionInfo> getInvitationsByUser(@PathVariable Long userId) {
        return invitationService.getInvitationsByUser(userId);
    }
}
