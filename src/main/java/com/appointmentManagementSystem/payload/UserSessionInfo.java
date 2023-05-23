package com.appointmentManagementSystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionInfo {

    private Long id;
    private Integer sessionCount;
    private Date date;
}
