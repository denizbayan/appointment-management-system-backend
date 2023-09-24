package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntitySessionPatientInvitation;
import com.appointmentManagementSystem.enums.EnumInvitationStatus;
import com.appointmentManagementSystem.enums.EnumVisitorStatus;
import com.appointmentManagementSystem.model.EntitySessionPatient;
import com.appointmentManagementSystem.payload.UserSessionInfo;
import com.appointmentManagementSystem.payload.UserInvitations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface InvitationRepository extends JpaRepository<EntitySessionPatientInvitation,Long> {

    @Query(value = "SELECT CASE  WHEN count(spi)> 0 THEN true ELSE false END FROM EntitySessionPatientInvitation spi where spi.sessionPatient=?1 and spi.status=?2  and spi.deleted=false")
    boolean existsBySessionPatientAndStatus(EntitySessionPatient sessionPatient, EnumInvitationStatus status);

    @Query(value = "SELECT CASE  WHEN count(spi)> 0 THEN true ELSE false END FROM EntitySessionPatientInvitation spi where spi.sessionPatient=?1 and spi.deleted=false")
    boolean existsBySessionPatient(EntitySessionPatient sessionPatient);

    @Query("select new com.appointmentManagementSystem.payload.UserInvitations(u.id as userId, sp.patientSession.id as sessionId, sp.id as sessionPatientId, spi.id as sessionPatientInvitationId , s.date as sessionDate) from EntityUser u \n"+
    "left join EntitySessionPatient sp on u.id = sp.user.id\n"+
    "left join EntitySessionPatientInvitation spi on sp.id = spi.sessionPatient.id\n"+
    "left join EntitySession s on s.id = sp.patientSession.id\n"+
    "where u.id=?1 and spi.id is not null and spi.deleted = false")
    Iterable<UserInvitations> findInvitationByUserId(long id);

    @Query("select new com.appointmentManagementSystem.payload.UserSessionInfo(s.id as id,sp.patientSession.sessionCount,s.date as date)  from EntitySessionPatient sp \n" +
            "left join EntityUser u on u.id = sp.user.id \n" +
            "left join EntitySession s on  s.id= sp.patientSession.id\n" +
            "where u.id = ?1 and s.date <= ?4 and  s.date > ?2 and sp.status=?3 and sp.deleted = false ")
    Iterable<UserSessionInfo> findSessionByUserId(long id, Date before, EnumVisitorStatus visitorStatus, Date now);

    @Query("select spi.id from EntitySessionPatientInvitation spi \n" +
            "where spi.sessionPatient.user.id= ?1 and spi.sessionPatient.patientSession.id = ?2 and spi.deleted=false ")
    Optional<Long> findInvitationByUserIdAndSessionId(long userID, long sessionID);

    @Query("select evi from EntitySessionPatientInvitation evi where evi.deleted=false")
    List<EntitySessionPatientInvitation> findAll();

    @Query("select spi from EntitySessionPatientInvitation spi where spi.id=?1 and spi.deleted=false")
    Optional<EntitySessionPatientInvitation> findById(Long id);

    @Transactional
    @Modifying
    @Query("update EntitySessionPatientInvitation spi set spi.deleted=true where spi.id=?1")
    Integer updateDeleted(Long InvitationID);

    @Transactional
    @Modifying
    @Query("update EntitySessionPatientInvitation spi set spi.link=:newlink where spi.sessionPatient.id=:sessionpatientid and spi.deleted = false")
    Integer UpdateLink(@Param("sessionpatientid") Long sessionPatientId,@Param("newlink") String newlink);

    @Query("select spi from EntitySessionPatientInvitation spi where spi.link like CONCAT('%',:token,'%') and spi.deleted = false")
    Optional<EntitySessionPatientInvitation> findByInvitationLink(@Param("token") String url);
}
