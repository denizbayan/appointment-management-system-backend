package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntitySessionPatient;
import com.appointmentManagementSystem.payload.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface SessionPatientRepository extends JpaRepository<EntitySessionPatient, Long> {

        @Query("select sp from EntitySessionPatient  sp where sp.id =?1 and sp.deleted = false")
        Optional<EntitySessionPatient> findById(Long id);

        @Query("select count(sp) from EntitySessionPatient sp where sp.session.id =?1 and sp.deleted = false")
        long countBySession_Id(Long sessionId);

        @Query("select sp from EntitySessionPatient sp where sp.deleted = false and sp.session.id =?1 ")
        List<EntitySessionPatient> findBySession_Id(Long exhibition_id );

        @Query("select new com.appointmentManagementSystem.payload.UserSessions(sp.user.id as userId, sp.session.id as sessionId,s.date as sessionDate , sp.id as sessionPatient ) from EntitySessionPatient sp \n"+
                "left join EntitySession s on s.id = sp.session.id \n"+
                "where sp.user.id =?1 and s.deleted = false")
        Iterable<UserSessions> findSessionsOfUser(Long userID);

        @Transactional
        @Modifying
        @Query("update EntitySessionPatient sp set sp.deleted=true where sp.id=?1")
        Integer Updatedeleted(Long exhibitionVisitorID);

}
