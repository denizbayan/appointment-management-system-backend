package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;

import java.util.List;

public interface BlogPostService {
    public List<EntityBlogPost> getPosts();

    public EntityBlogPost savePost(AddBlogPostPayload newBlogPost);

    public Long deletePost(Long postID);


}
