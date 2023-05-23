package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntitySessionPatientInvitation;
import com.appointmentManagementSystem.payload.UserSessionInfo;


public interface InvitationService {

    EntitySessionPatientInvitation createInvitation(Long visitorID) throws Exception;

    Iterable<EntitySessionPatientInvitation> findAll();

    void deleteInvitationById(long id);

    Iterable<UserSessionInfo> getInvitationsByUser(long id);

    void setUsedToken(String token);
}
