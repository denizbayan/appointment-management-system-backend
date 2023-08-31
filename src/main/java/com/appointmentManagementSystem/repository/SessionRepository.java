package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntitySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Repository
public interface SessionRepository extends JpaRepository<EntitySession,Long> {

    @Query("select e from EntitySession e where e.id =?1 and e.deleted =?2")
    Optional<EntitySession> findByIdAndDeleted(Long id,boolean deleted);

    @Query("select e from EntitySession e where e.deleted = false and e.date <:maxdate order by e.date desc")
    List<EntitySession> findAll(@Param("maxdate")Date maxdate);

    @Query("select e from EntitySession e where e.deleted = false and e.patient_user.user.id =?1 order by e.date desc")
    List<EntitySession> findByUserId(Long id);


    @Query("select e from EntitySession e where e.date > ?1 and e.deleted = false order by e.date asc")
    List<EntitySession> findAllByDateAfter(Date ActivenessLimit);

    List<EntitySession> findAllByDeleted(boolean deleted);

    @Transactional
    @Modifying
    @Query("update EntitySession e set e.deleted=true where e.id=?1")
    Void updateDeleted(Long exhibitionID);

}
