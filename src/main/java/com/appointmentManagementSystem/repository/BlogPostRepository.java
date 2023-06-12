package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityBlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<EntityBlogPost,Long> {

    List<EntityBlogPost> findAllByDeleted(boolean deleted);

    Optional<EntityBlogPost> findByIdAndDeleted(Long id, boolean deleted);

    @Transactional
    @Modifying
    @Query("update EntityBlogPost bp set bp.deleted=true where bp.id=?1")
    int Updatedeleted(Long postID);
}
