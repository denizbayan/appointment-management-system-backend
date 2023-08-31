package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.AddSesssionPayload;

import java.util.List;

public interface SessionService {
    List<EntitySession> findAll(String purpose);

    List<EntitySession> findSessionByUserId(Long userId);

    EntitySession saveSession(AddSesssionPayload  session) ;

    void deleteSessionById(long id) throws Exception;

}
