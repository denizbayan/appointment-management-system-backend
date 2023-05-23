package com.appointmentManagementSystem.payload;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class InvitationPayload {

    private String fullName;
    private Date date;
    private String qrCode;
    private String invitationCode;
    private String status;
    private String creatorName;
    /*
    StringBuilder base64 = new StringBuilder("data:image/png;base64,");
        base64.append(Base64.getEncoder().encodeToString(photo));
        dto.setPhoto(base64.toString());
*/
}