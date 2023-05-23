package com.appointmentManagementSystem.repository;

import com.appointmentManagementSystem.model.EntityConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ConstantsRepository extends JpaRepository<EntityConstant,Long> {

    Optional<EntityConstant> findByKey(String key);
}
