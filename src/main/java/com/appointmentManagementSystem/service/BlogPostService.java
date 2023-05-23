package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;

import java.util.List;

public interface BlogPostService {
    public List<EntityBlogPost> getPosts();

    public EntityBlogPost addPost(AddBlogPostPayload newBlogPost);

    public Long deletePost(Long postID);

    public EntityBlogPost updatePost(Long postID, AddBlogPostPayload newBlogPost);

}
