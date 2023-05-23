package com.appointmentManagementSystem.config;

import com.appointmentManagementSystem.service.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UsernameAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = null;
        String username = "anonymousUser";
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        try {
            principal=  (UserDetailsImpl) authentication.getPrincipal();
            username  = principal.getUsername();
        }catch (Exception e){

        }


        return Optional.ofNullable(username);
    }
}