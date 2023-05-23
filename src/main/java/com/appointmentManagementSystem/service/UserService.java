package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.payload.SignupRequest;
import com.appointmentManagementSystem.util.CustomException;
import com.appointmentManagementSystem.model.EntityUser;

import java.util.Optional;

public interface UserService {

    EntityUser addUser(SignupRequest user);

    Iterable<EntityUser> findAll();

    Optional<EntityUser> getUserById(Long id);

    String getAvatar();

    Boolean setAvatar(String avatar);

    void deleteUserById(long id) throws CustomException;

    boolean existsByEmail(String mail);

    EntityUser updateUser(SignupRequest req,Long id) throws CustomException;
}
