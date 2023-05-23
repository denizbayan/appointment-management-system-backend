package com.appointmentManagementSystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInvitations {
    private Long userId;
    private Long sessionId;
    private Long sessionPatientId;
    private Long sessionPatientInvitationId;
    private Date sessionDate;

}
