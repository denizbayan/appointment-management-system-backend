package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityBlogPostInteraction;
import com.appointmentManagementSystem.payload.BlogInteractionResponsePayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BlogPostInteractionRepository extends JpaRepository<EntityBlogPostInteraction,Long> {

    @Transactional
    @Modifying
    @Query("update EntityBlogPostInteraction bpi set bpi.deleted=true where bpi.id=?1")
    int updateDeleted(Long interaction_id);

    Optional<EntityBlogPostInteraction> findByIdAndDeleted(Long interaction_id, Boolean deleted);

    List<EntityBlogPostInteraction> findAllByUser_IdAndDeleted(Long user_id,Boolean deleted);

    List<EntityBlogPostInteraction> findAllByPost_IdAndDeleted(Long post_id,Boolean deleted);

    List<EntityBlogPostInteraction> findAllByUser_IdAndInteractionTypeAndDeleted(Long user_id, EnumBlogInteractionType interactionType, Boolean deleted);

    List<EntityBlogPostInteraction> findAllByPost_IdAndInteractionTypeAndDeleted(Long post_id, EnumBlogInteractionType interactionType, Boolean deleted);

    @Query("select AVG(CAST(bpi.interactionValue AS float )) from EntityBlogPostInteraction bpi where bpi.post.id=?1 and bpi.deleted =?2 and bpi.interactionType=?3")
    Float findAverageRateByPostID(Long post_id,boolean deleted, EnumBlogInteractionType interactionType);

    @Query("select new com.appointmentManagementSystem.payload.BlogInteractionResponsePayload(bpi.id as id, bpi.createdBy as author, bpi.createdDate as createdAt, bpi.lastModifiedDate as updatedAt, bpi.interactionValue as interactionValue) from EntityBlogPostInteraction bpi \n"
    +" where bpi.post.id=?1 and bpi.deleted =?2 and bpi.interactionType=?3")
    ArrayList<BlogInteractionResponsePayload> findCommentsByPostID(Long post_id, boolean deleted, EnumBlogInteractionType interactionType);

}
