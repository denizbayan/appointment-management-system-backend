package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityBlogPostInteraction;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.GetBlogPostsResponsePayload;
import com.appointmentManagementSystem.repository.BlogPostInteractionRepository;
import com.appointmentManagementSystem.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class BlogPostServiceImpl implements BlogPostService {

    BlogPostRepository blogPostRepository;

    BlogPostInteractionRepository blogPostInteractionRepository;

    @Autowired
    public BlogPostServiceImpl(BlogPostRepository blogPostRepository, BlogPostInteractionRepository blogPostInteractionRepository) {

        this.blogPostRepository=blogPostRepository;
        this.blogPostInteractionRepository = blogPostInteractionRepository;

    }

    public List<GetBlogPostsResponsePayload> getPosts(){
        List<EntityBlogPost> posts =  blogPostRepository.findAllByDeleted(false);

        List<GetBlogPostsResponsePayload> responsePosts = new ArrayList<>();

        for (EntityBlogPost post:posts){
            GetBlogPostsResponsePayload responsePost = new GetBlogPostsResponsePayload();
            responsePost.setId(post.getId());
            responsePost.setTitle(post.getTitle());
            responsePost.setContent(post.getContent());

            responsePost.setAuthor(post.getCreatedBy());
            responsePost.setCreatedAt(post.getCreatedDate());
            responsePost.setUpdatedAt(post.getLastModifiedDate());
            Float rate = blogPostInteractionRepository.findAverageRateByPostID(post.getId(),false,EnumBlogInteractionType.Rate);
            System.out.println(rate);
            responsePost.setRate(rate);
            responsePost.setComments(blogPostInteractionRepository.findCommentsByPostID(post.getId(),false,EnumBlogInteractionType.Comment));
            responsePosts.add(responsePost);
        }


        return  responsePosts;
    }

    public EntityBlogPost savePost(AddBlogPostPayload newBlogPost){

        if(newBlogPost.getId() == -1L){
            EntityBlogPost newPost = new EntityBlogPost();
            newPost.setDeleted(false);
            newPost.setContent(newBlogPost.getContent());
            newPost.setTitle(newBlogPost.getTitle());

            return blogPostRepository.save(newPost);
        }else{
            Optional<EntityBlogPost> bp = blogPostRepository.findByIdAndDeleted(newBlogPost.getId(),false);
            if (bp.isPresent()){
                EntityBlogPost existingPost = bp.get();
                existingPost.setContent(newBlogPost.getContent());
                existingPost.setTitle(newBlogPost.getTitle());

                return blogPostRepository.save(existingPost);
            }else{
                return null;
            }
        }

    }

    public Long deletePost(Long postID){return blogPostRepository.Updatedeleted(postID)==0?0L:1L;}


}

