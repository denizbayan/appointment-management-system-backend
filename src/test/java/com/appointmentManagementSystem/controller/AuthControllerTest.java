package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.controller.AuthController;
import com.appointmentManagementSystem.repository.RoleRepository;
import com.appointmentManagementSystem.repository.UserRepository;
import com.appointmentManagementSystem.security.JwtUtils;
import com.appointmentManagementSystem.service.InvitationService;
import com.appointmentManagementSystem.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

//  @ExtendWith(SpringExtension.class)
//@WebMvcTest(AuthController.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {


    @Autowired
    MockMvc mvc;

    @InjectMocks
    AuthController controller;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PasswordResetService passwordResetService;

    @Mock
    InvitationService invitationService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtUtils jwtUtils;

    @Test
    void authenticateUser() {
    }

    @Test
    void registerUser() {
    }

    @Test
    void validateToken() {
    }

    @Test
    void sendSecurityCode() {
    }

    @Test
    void testSendSecurityCode() {
    }
}