package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.GetBlogPostsResponsePayload;
import com.appointmentManagementSystem.payload.InteractPostPayload;
import com.appointmentManagementSystem.service.BlogPostInteractionService;
import com.appointmentManagementSystem.service.BlogPostService;
import com.appointmentManagementSystem.payload.MessageResponse;
import com.appointmentManagementSystem.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    BlogPostInteractionService blogPostInteractionService;

    @GetMapping("/getPosts")
    public List<GetBlogPostsResponsePayload> getBlogPosts(){
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

    @PostMapping("/interactPost")
    @PreAuthorize(" hasRole('USER')")
    public ResponseEntity<MessageResponse> interactPost(@RequestBody InteractPostPayload interactPostPayload){

        try{
            blogPostInteractionService.savePostInteraction(interactPostPayload);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(
                    interactPostPayload.getInteractionType()== EnumBlogInteractionType.Comment?"Yorumunuz başarıyla kaydedilmiştir.":"Değerlendirmeniz başarıyla kaydedilmiştir."
            ));
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(
                    e.getMessage()
            ));
        }

    }

    @DeleteMapping("/deletePostInteraction/{interactionID}")
    @PreAuthorize(" hasRole('USER')")
    public ResponseEntity<MessageResponse> deletePostInteraction(@PathVariable long interactionID){
        blogPostInteractionService.deletePostInteraction(interactionID);
        return null;
    }

}
