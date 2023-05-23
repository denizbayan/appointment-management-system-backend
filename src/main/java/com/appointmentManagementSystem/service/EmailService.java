package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityUser;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

public interface EmailService {

    //void sendSimpleMessage(String to, String subject, String text) throws MessagingException, URISyntaxException;

    void sendEmailHTML(String to, String subject, byte[] attachment, String str, Map<String,Object> variables) throws MessagingException, IOException, URISyntaxException;

    void sendPasswordResetCode(String to, String subject, Map<String, Object> variables) throws MessagingException, IOException, URISyntaxException;

    String createCal(EntityUser user, Date sessionDate) throws MessagingException, IOException, URISyntaxException;

    void sendEmailHTMLForUpdate(String email, String s,Map<String, Object> model, String iCalString) throws MessagingException;

}
