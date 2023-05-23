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

    public EntityBlogPost addPost(AddBlogPostPayload newBlogPost){
        EntityBlogPost newPost = new EntityBlogPost();
        newPost.setDeleted(false);
        newPost.setContent(newBlogPost.getContent());
        newPost.setTitle(newBlogPost.getTitle());

        return blogPostRepository.save(newPost);

    }

    public Long deletePost(Long postID){return blogPostRepository.Updatedeleted(postID)==0?0L:1L;}

    public EntityBlogPost updatePost(Long postID, AddBlogPostPayload newBlogPost){

        Optional<EntityBlogPost> bp = blogPostRepository.findById(postID);

        if(bp.isPresent()){
            EntityBlogPost blogPost = bp.get();
            blogPost.setContent(newBlogPost.getContent());
            blogPost.setTitle(newBlogPost.getTitle());
            return blogPostRepository.save(blogPost);

        }else{
            return null;
        }

    }

}
