package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.payload.ChangePasswordRequest;
import com.appointmentManagementSystem.payload.ResetPasswordRequest;

public interface PasswordResetService {

    void sendSecurityCode(String email) throws Exception;

    boolean resetPassword(ResetPasswordRequest resetPasswordRequest) throws Exception;

    boolean ChangePassword(ChangePasswordRequest changePasswordRequest) throws Exception;
}
