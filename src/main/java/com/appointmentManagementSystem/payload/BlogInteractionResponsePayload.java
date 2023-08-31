package com.appointmentManagementSystem.payload;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BlogInteractionResponsePayload {

    private Long id;

    public String author;
    public Date createdAt;
    public Date updatedAt;

    public String interactionValue;

}
