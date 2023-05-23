package com.appointmentManagementSystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessions {
    private Long userId;
    private Long sessionId;
    private Date sessionDate;
    private Long sessionPatientId;

}
