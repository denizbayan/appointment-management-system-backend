package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityRole;
import com.appointmentManagementSystem.enums.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<EntityRole,Long> {
    Optional<EntityRole> findByRoleName(EnumRole name);

}
