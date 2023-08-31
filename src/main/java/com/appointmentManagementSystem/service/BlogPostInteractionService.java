package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPostInteraction;
import com.appointmentManagementSystem.payload.InteractPostPayload;
import com.appointmentManagementSystem.util.CustomException;

public interface BlogPostInteractionService {

    public EntityBlogPostInteraction savePostInteraction(InteractPostPayload ratePostPayload) throws CustomException;

    public Long deletePostInteraction(Long interactionID);
}
