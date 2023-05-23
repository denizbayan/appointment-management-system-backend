package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.repository.RoleRepository;
import com.appointmentManagementSystem.enums.EnumRole;
import com.appointmentManagementSystem.model.EntityRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    //override method in RoleService
    // use roleRepository .findby...
    //EntityRole object create and save to roleRepository

    @Override
    public void init(){
        List<EntityRole> entityRoles = new ArrayList<>();
        entityRoles.add(new EntityRole(1, EnumRole.ROLE_USER));
        entityRoles.add(new EntityRole(2, EnumRole.ROLE_ADMIN));

        List<EntityRole> dbEntityRoles =    roleRepository.findAll();
        for(EntityRole role : dbEntityRoles ) {
            entityRoles.remove(role);
        }
        for(EntityRole role : entityRoles){
            roleRepository.save(role);
        }


    }

}
