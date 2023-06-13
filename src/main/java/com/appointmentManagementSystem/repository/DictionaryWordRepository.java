package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityDictionaryWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface DictionaryWordRepository extends JpaRepository<EntityDictionaryWord,Long> {

    List<EntityDictionaryWord> findAllByDeleted(boolean deleted);

    Optional<EntityDictionaryWord> findByIdAndDeleted(Long id, boolean deleted);

    @Transactional
    @Modifying
    @Query("update EntityDictionaryWord dw set dw.deleted=true where dw.id=?1")
    int Updatedeleted(Long postID);
}
