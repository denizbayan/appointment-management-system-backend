package com.appointmentManagementSystem.service;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VAlarm;
import biweekly.component.VEvent;
import biweekly.parameter.ParticipationLevel;
import biweekly.parameter.ParticipationStatus;
import biweekly.parameter.Related;
import biweekly.parameter.Role;
import biweekly.property.*;
import biweekly.util.Duration;
import com.appointmentManagementSystem.repository.SessionPatientRepository;
import com.appointmentManagementSystem.model.EntitySession;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.util.Constant;
import com.appointmentManagementSystem.util.ImageUtils;
import com.appointmentManagementSystem.util.QrCodeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.*;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import javax.mail.BodyPart;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class EmailServiceImpl implements EmailService{

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    SessionPatientRepository sessionPatientRepository;


    InputStreamSource instagramResource = new InputStreamSource() {
        @Override
        public InputStream getInputStream() throws IOException {
            return this.getClass().getResourceAsStream("/static/images/instagram2x.png");
        }
    };
    InputStreamSource linkedinResource = new InputStreamSource() {
        @Override
        public InputStream getInputStream() throws IOException {
            return this.getClass().getResourceAsStream("/static/images/linkedin2x.png");
        }
    };
    InputStreamSource twitterResource = new InputStreamSource() {
        @Override
        public InputStream getInputStream() throws IOException {
            return this.getClass().getResourceAsStream("/static/images/twitter2x.png");
        }
    };

    /*@Override
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        message.setFrom(Constant.EMAIL_FROM_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        helper.addInline("instagramLogo",instagramResource,"image/*" );
        helper.addInline("linkedinLogo",linkedinResource,"image/*" );
        helper.addInline("twitterLogo",twitterResource,"image/*" );

        emailSender.send(message);
    }*/

    @Override
    public void sendEmailHTML(String to, String subject, byte[] attachment, String str, Map<String, Object> variables) throws MessagingException, IOException, URISyntaxException {
        ByteArrayDataSource ds = new ByteArrayDataSource(attachment, "image/*");
        MimeMessage message = emailSender.createMimeMessage();
        message.setContentID("barkod");
        BodyPart body =new MimeBodyPart();
        body.setDataHandler(new DataHandler(ds));
        body.setHeader("Content-ID","<barkod>");
        body.setDisposition("inline");
        Multipart image = new  MimeMultipart();

        image.addBodyPart(body);
        message.setContent(image);
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        if(attachment !=null) {
            helper.addAttachment("SessionLinkQRcode.jpg", new ByteArrayResource(attachment));
        }else if (variables.get("qrCode") != null){
            helper.addAttachment("SessionLinkQRcode.jpg", new ByteArrayResource((byte[])variables.get("qrCode")));
        }
        Context context = new Context();
        context.setVariables(variables);
        //String html = templateEngine.process("invitationMail", context);
        String html = templateEngine.process("ams_davet", context);
        helper.setTo(to);
        helper.setText(html, true);

        DataSource iCalData = new ByteArrayDataSource(str,"text/calendar; charset=UTF-8");
        helper.addAttachment("takvim.ics",iCalData);

        helper.addInline("instagramLogo",instagramResource,"image/*" );
        helper.addInline("linkedinLogo",linkedinResource,"image/*" );
        helper.addInline("twitterLogo",twitterResource,"image/*" );


        helper.setSubject(subject);
        helper.setFrom(Constant.EMAIL_FROM_ADDRESS);
        emailSender.send(message);
    }

    @Override
    public void sendPasswordResetCode(String to, String subject,Map<String, Object> variables) throws MessagingException, IOException, URISyntaxException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("resetMail", context);
        helper.setTo(to);
        helper.setText(html, true);


        helper.setSubject(subject);
        helper.setFrom(Constant.EMAIL_FROM_ADDRESS);

        emailSender.send(message);
    }

    @Override
    public void sendEmailHTMLForUpdate(String email, String subject,Map<String, Object> variables, String iCalString) throws MessagingException {
        try
        {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,

                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("updateExhibition", context);
            helper.setTo(email);
            helper.setText(html, true);

            helper.addInline("instagramLogo",instagramResource,"image/*" );
            helper.addInline("linkedinLogo",linkedinResource,"image/*" );
            helper.addInline("twitterLogo",twitterResource,"image/*" );
            if(!iCalString.equals("")){
                DataSource iCalData = new ByteArrayDataSource(iCalString,"text/calendar; charset=UTF-8");
                helper.addAttachment("takvim.ics",iCalData);
            }

            try {
                String str = (String)variables.get("link");
                BufferedImage encode = QrCodeUtil.encode(str, null, false);
                byte[] jpgs = ImageUtils.toByteArray(encode, "jpg");
                helper.addAttachment("SessionLinkQRcode.jpg", new ByteArrayResource(jpgs));
            }catch (Exception e ){}

            helper.setSubject(subject);
            helper.setFrom(Constant.EMAIL_FROM_ADDRESS);

            emailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
            throw  new RuntimeException("Mail gönderim hatası. Bazı kullanıcılara mail gitmemiş olabilir. Lütfen Etkinliği tekrar güncelleyiniz.");

        }


    }

    /****************************** HELPER METHODS ******************************/

    public String createCal(EntityUser user, Date sessionDate) throws MessagingException, ParseException, IOException, URISyntaxException {
        TimeZone trTimezone =  TimeZone.getTimeZone("GMT");
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();
        Attendee attendee = new Attendee(user.getFullname(), user.getEmail());
        attendee.setRsvp(true);
        attendee.setRole(Role.ATTENDEE);
        attendee.setParticipationStatus(ParticipationStatus.CONFIRMED);
        attendee.setParticipationLevel(ParticipationLevel.REQUIRED);
        event.addAttendee(attendee);
        event.setSummary("PSK. Gamze Bayan Seans");
        event.setDateStart(sessionDate);
        event.setDateEnd(DateUtils.addHours(sessionDate,24));

        Duration reminder = new Duration.Builder().minutes(15).build();
        Trigger trigger = new Trigger(reminder, Related.START);
        Action action = new Action("DISPLAY");
        VAlarm valarm = new VAlarm(action, trigger);
        event.addAlarm(valarm);
        Duration duration = new Duration.Builder().hours(24).build();
        event.setDuration(duration);
        event.setUid("555xxx");
        event.setOrganizer(Constant.EMAIL_FROM_ADDRESS);
        event.setLocation("PSK. Gamze Bayan sitesi");
        ical.addEvent(event);
        ical.setMethod(Method.REQUEST);
        String str = Biweekly.write(ical).tz(trTimezone,false).go();
        return str;
    }


    private Map<String, Object> InitializeHTMLParameters(Long surveyId, EntitySession session){
        Map<String, Object> model= new HashMap<String, Object>();

        Instant instant = session.getDate().toInstant();
        ZoneId z = ZoneId.of ("Europe/Istanbul");
        ZonedDateTime istanbul = instant.atZone(z);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedString = istanbul.format(formatter);
        model.put("sessionDate",formattedString);

        model.put("sign", "PSK. Gamze Bayan Seansı");
        return model;
    }
}
