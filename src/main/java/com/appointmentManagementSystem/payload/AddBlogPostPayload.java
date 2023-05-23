package com.appointmentManagementSystem.payload;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddBlogPostPayload {

    public String title;
    public String content;
}
