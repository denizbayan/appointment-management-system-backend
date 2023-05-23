package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.AddSesssionPayload;

import java.util.List;

public interface SessionService {
    List<EntitySession> findAll(String purpose);

    EntitySession addSession(AddSesssionPayload session) throws Exception;

    EntitySession updateSession(AddSesssionPayload  session) throws Exception;

    Integer addPatientToSession(Long sessionId, List<EntityUser> visitors) throws Exception;

    void deleteSessionById(long id) throws Exception;

    List<EntityUser> getUsersFromIdList(List<Long> idList);
}
