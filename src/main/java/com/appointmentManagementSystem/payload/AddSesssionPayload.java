package com.appointmentManagementSystem.payload;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class AddSesssionPayload {

    private Long sessionID;
    private Long patientID;
    private Date date;
    private String name;

}
