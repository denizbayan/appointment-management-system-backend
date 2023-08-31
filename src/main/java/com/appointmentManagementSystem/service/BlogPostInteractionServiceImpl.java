package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityBlogPostInteraction;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.payload.InteractPostPayload;
import com.appointmentManagementSystem.repository.BlogPostInteractionRepository;
import com.appointmentManagementSystem.repository.BlogPostRepository;
import com.appointmentManagementSystem.repository.UserRepository;
import com.appointmentManagementSystem.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BlogPostInteractionServiceImpl implements BlogPostInteractionService {

    BlogPostInteractionRepository blogPostInteractionRepository;
    BlogPostRepository blogPostRepository;

    UserRepository userRepository;

    @Autowired
    public BlogPostInteractionServiceImpl(BlogPostInteractionRepository blogPostInteractionRepository, BlogPostRepository blogPostRepository, UserRepository userRepository) {
        this.blogPostInteractionRepository=blogPostInteractionRepository;
        this.blogPostRepository=blogPostRepository;
        this.userRepository = userRepository;
    }

    public EntityBlogPostInteraction savePostInteraction(InteractPostPayload interactPostPayload) throws CustomException{


        if(interactPostPayload.getInteractionID() == -1L){
            EntityBlogPostInteraction newInteraction = new EntityBlogPostInteraction();
            newInteraction.setDeleted(false);
            Optional<EntityBlogPost> bp = blogPostRepository.findById(interactPostPayload.getPostID());
            if (bp.isPresent()){
                newInteraction.setPost(bp.get());
            }else{
                throw new CustomException("Blog bulunamadı, sayfayı yenileyip tekrar deneyiniz.");
            }

            Optional<EntityUser> u = userRepository.findById(interactPostPayload.getUserID());
            if (u.isPresent()){
                newInteraction.setUser(u.get());
            }else{
                throw new CustomException("Kullanıcı bulunamadı, çıkış yapıp tekrar giriniz.");
            }

            newInteraction.setInteractionType(interactPostPayload.getInteractionType());
            newInteraction.setInteractionValue(interactPostPayload.getInteractionValue());

            return blogPostInteractionRepository.save(newInteraction);
        }else{
            Optional<EntityBlogPostInteraction> bpi = blogPostInteractionRepository.findByIdAndDeleted(interactPostPayload.getInteractionID(),false);
            if (bpi.isPresent()){
                EntityBlogPostInteraction existingInteraction = bpi.get();
                existingInteraction.setInteractionValue(interactPostPayload.getInteractionValue());

                return blogPostInteractionRepository.save(existingInteraction);
            }else{
                String interactionType = interactPostPayload.getInteractionType()== EnumBlogInteractionType.Comment?"Yorumunuz":"Değerlendirmeniz";
                throw new CustomException("Düzenlemeye çalıştığınız " +interactionType + " bulunamadı, sayfayı yenileyip tekrar deneyiniz.");
            }
        }



    }

    public Long deletePostInteraction(Long interactionID){return blogPostInteractionRepository.Updatedeleted(interactionID)==0?0L:1L;}

}

