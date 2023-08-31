package com.appointmentManagementSystem.payload;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityUser;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InteractPostPayload {
    Long interactionID;
    Long userID;
    Long postID;
    EnumBlogInteractionType interactionType;
    String interactionValue;

}
