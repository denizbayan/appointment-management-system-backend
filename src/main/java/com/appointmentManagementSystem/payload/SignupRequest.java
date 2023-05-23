package com.appointmentManagementSystem.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;
@ToString
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 40)
    private String fullname;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    private String cellPhone;

    private String gender;

    private Date birthdate;

    private long avatarId;

    private Long id;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
