package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.GetBlogPostsResponsePayload;

import java.util.List;

public interface BlogPostService {
    public List<GetBlogPostsResponsePayload> getPosts();

    public EntityBlogPost savePost(AddBlogPostPayload newBlogPost);

    public Long deletePost(Long postID);

}
