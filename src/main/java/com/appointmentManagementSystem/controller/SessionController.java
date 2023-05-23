package com.appointmentManagementSystem.controller;

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



    @PostMapping("/getSessions")
    @PreAuthorize(" hasRole('ADMIN')")
    public List<EntitySession> getSessions(@RequestBody String purpose) {
            return sessionService.findAll(purpose);
    }


    @PostMapping("/addSession")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<Long> addSession(@RequestBody AddSesssionPayload req){
        try{
            return new ResponseEntity<Long>(sessionService.addSession(req).getId(), HttpStatus.CREATED);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof MessagingException) { // email error
                return new ResponseEntity<Long>(-1L, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (e instanceof CustomException && e.getMessage().contains("dolu")) { // 8 exhibitions active at the moment
                return new ResponseEntity<Long>(-2L, HttpStatus.INTERNAL_SERVER_ERROR);
            }else if (e instanceof CustomException && e.getMessage().contains("aynı isim")) { // same exhibition name in active timeline
                return new ResponseEntity<Long>(-3L, HttpStatus.INTERNAL_SERVER_ERROR);
            }else { // unknown error
                return new ResponseEntity<Long>(-4L, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/updateSession")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateSession(@RequestBody AddSesssionPayload req){

        try {
            EntitySession newExhi = sessionService.updateSession(req);
            if(newExhi == null){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Hata: Etkinlik bulunamadı!"));
            }else {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof MessagingException) {
                return new ResponseEntity<MessageResponse>(new MessageResponse("Mail gönderme başarısız oldu.Lütfen mail adreslerini kontrol ediniz"), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (e instanceof CustomException){
                return new ResponseEntity<MessageResponse>(new MessageResponse(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<MessageResponse>(new MessageResponse("Mail gönderme başarısız oldu. Bazı kullanıcılara mail gitmemiş olabilir. Lütfen etkinliği tekrar güncelleyiniz."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/addPatientToSession/{sessionId}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addVisitorsToExhibition(@PathVariable Long sessionId,@RequestBody List<Long> visitorIdList){
        try{
            List<EntityUser> userList = sessionService.getUsersFromIdList(visitorIdList);
            sessionService.addPatientToSession(sessionId,userList);
            return new ResponseEntity<>(new MessageResponse(""), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Katılımcı etkinliğe eklenemedi, lütfen tekrar deneyiniz."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteSessionById/{id}")
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
