package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BlogPostServiceImpl implements BlogPostService {

    BlogPostRepository blogPostRepository;

    @Autowired
    public BlogPostServiceImpl(BlogPostRepository blogPostRepository) {
        this.blogPostRepository=blogPostRepository;
    }

    public List<EntityBlogPost> getPosts(){
        return blogPostRepository.findAllByDeleted(false);
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

