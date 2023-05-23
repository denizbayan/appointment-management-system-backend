package com.appointmentManagementSystem.payload;

import lombok.ToString;

import java.util.List;

@ToString

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private List<String> roles;
    private Long surveyFilledStatus; // -1: not survey token, 0: survey token and not filled survey yet, 1: survey token and filled

    public JwtResponse(String accessToken, Long id, String fullName, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Long getSurveyFilledStatus() {
        return surveyFilledStatus;
    }

    public void setSurveyFilledStatus(Long surveyFilledStatus) {
        this.surveyFilledStatus = surveyFilledStatus;
    }
}
