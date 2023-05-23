package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityRole;
import com.appointmentManagementSystem.model.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<EntityUser,Long> {

    @Query("select u from EntityUser u where u.fullname =?1  and u.deleted=false")
    Optional<EntityUser> findByUsername(String fullname);

    @Query("select u from EntityUser u where u.email =?1  and u.deleted=false")
    Optional<EntityUser> findByEmail(String mail);

    @Query(value = "SELECT CASE  WHEN count(u)> 0 THEN true ELSE false END FROM EntityUser u where u.fullname =?1  and u.deleted=false")
    Boolean existsByUsername(String fullname);

    @Query(value = "SELECT CASE  WHEN count(u)> 0 THEN true ELSE false END FROM EntityUser u where u.fullname =?1  and u.deleted=true")
    Boolean existsbyDeleted(String fullname);

    @Query(value = "SELECT CASE  WHEN count(u)> 0 THEN true ELSE false END FROM EntityUser u where u.email =?1  and u.deleted=false")
    Boolean existsByEmail(String email);

    @Query("select u from EntityUser u where u.deleted=false")
    List<EntityUser> findAll();

    @Query("select u from EntityUser u where u.deleted=false and :role member of  u.role")
    List<EntityUser> findByRole(@Param("role")EntityRole role);

    @Query("select u from EntityUser u JOIN u.role rl where u.fullname =?1 and rl=?2  and u.deleted=false")
    Iterable<EntityUser> findByUsernameContainingAndRoleIn(@NotBlank @Size(max = 50) String fullname, Set<EntityRole> role);

    @Transactional
    @Modifying
    @Query("update EntityUser u set u.guestInvitationLink =:newlink where u.id=:userid")
    Integer updateGuestInvitationLinkByUserID(@Param("userid") Long userID, @Param("newlink") String newGuestInvitationLink);
}
