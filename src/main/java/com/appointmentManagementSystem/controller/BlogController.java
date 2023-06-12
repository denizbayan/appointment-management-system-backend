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

    @GetMapping("/getPosts")
    public List<EntityBlogPost> getBlogPosts(){
        return blogPostService.getPosts();

    }

    @DeleteMapping("/deletePost/{postID}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteBlogPost(@PathVariable long postID){
        blogPostService.deletePost(postID);
        return null;
    }

    @PostMapping("/savePost")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateBlogPost(@RequestBody AddBlogPostPayload newBlogPost){
        EntityBlogPost response = blogPostService.savePost(newBlogPost);

        if (response == null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Güncellemek istediğiniz blog içeriği bulunamamıştır. Lütfen sayfanızı yeniledikten sonra tekrar deneyiniz."));
        }else{
            if(newBlogPost.getId() == -1){
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Blog başarıyla eklenmiştir."));
            }else{
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Blog başarıyla güncellenmiştir."));
            }

        }


    }

}
