package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Transactional
@Repository
public interface ResetPasswordRepository extends JpaRepository<EntityResetPassword, Long> {

    Set<EntityResetPassword> findByCode(String code);
}
