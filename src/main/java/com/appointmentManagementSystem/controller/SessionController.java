package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.model.EntityDictionaryWord;
import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.AddSesssionPayload;
import com.appointmentManagementSystem.payload.MessageResponse;
import com.appointmentManagementSystem.payload.*;
import com.appointmentManagementSystem.service.SessionService;
import com.appointmentManagementSystem.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    public SessionService sessionService;



    @GetMapping("/getSessions/{purpose}")
    @PreAuthorize(" hasRole('ADMIN')")
    public List<EntitySession> getSessions(@PathVariable String purpose) {
            return sessionService.findAll(purpose);
    }

    @GetMapping("/getSessionsByUserId/{id}")
    @PreAuthorize(" hasRole('USER')")
    public List<EntitySession> getSessions(@PathVariable Long id) {
        return sessionService.findSessionByUserId(id);
    }


    @PostMapping("/saveSession")
    @PreAuthorize(" hasRole('USER')")
    public ResponseEntity<MessageResponse> saveSession(@RequestBody AddSesssionPayload sessionReq){

        EntitySession response = sessionService.saveSession(sessionReq);

        if (response == null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Güncellemek istediğiniz seans bulunamamıştır. Lütfen sayfanızı yeniledikten sonra tekrar deneyiniz."));
        }else{
            if(sessionReq.getSessionID() == -1){
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Seans başarıyla eklenmiştir."));
            }else{
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Seans başarıyla güncellenmiştir."));
            }

        }
    }


    @DeleteMapping("/deleteSessionById/{id}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteSessionById(@PathVariable("id") long id) {
        try {
            sessionService.deleteSessionById(id);
            return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
