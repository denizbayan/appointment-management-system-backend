package com.appointmentManagementSystem.payload;

import com.appointmentManagementSystem.enums.EnumSessionStatus;
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
    private Long psychologistID;
    private Date sessionDate;
    private EnumSessionStatus status;

}
