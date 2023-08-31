package com.appointmentManagementSystem.payload;

import com.appointmentManagementSystem.model.EntityBlogPostInteraction;
import com.appointmentManagementSystem.model.EntityUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetBlogPostsResponsePayload {

    private Long id;
    public String title;
    public String content;

    public String author;
    public Date createdAt;
    public Date updatedAt;

    public Float rate;
    public ArrayList<BlogInteractionResponsePayload> comments;

}
