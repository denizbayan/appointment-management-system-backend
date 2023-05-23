package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.service.BlogPostService;
import com.appointmentManagementSystem.payload.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired
    BlogPostService blogPostService;

    @PostMapping("/addPost")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addBlogPost(@RequestBody AddBlogPostPayload newBlogPost){
        blogPostService.addPost(newBlogPost);
        return null;
    }

    @GetMapping("/getPosts")
    public List<EntityBlogPost> getBlogPosts(){
        return blogPostService.getPosts();

    }

    @PostMapping("/deletePost/{postID}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteBlogPost(@PathVariable long postID){
        blogPostService.deletePost(postID);
        return null;
    }

    @PostMapping("/updatePost/{postID}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateBlogPost(@PathVariable long postID , @RequestBody AddBlogPostPayload newBlogPost){
        blogPostService.updatePost(postID,newBlogPost);
        return null;
    }

}
