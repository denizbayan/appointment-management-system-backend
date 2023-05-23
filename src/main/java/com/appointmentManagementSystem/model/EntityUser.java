package com.appointmentManagementSystem.model;


import com.appointmentManagementSystem.enums.EnumUserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ToString
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="users",uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
}
)
public class EntityUser extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max=50)
    private String fullname;

    @Size(max=20)
    @Pattern(regexp="(^$|[0-9]{10})")
    private String cellPhone;

    @Enumerated
    private EnumUserStatus status = EnumUserStatus.Aktif;

    @Column
    private String gender;

    @Column
    private Date birthdate;

    @Column
    private Integer avatarId = 1;

    @Column
    private Integer passwordFailCount = 0;

    @Column
    private Date banLoginUntil;

    @NotBlank
    @Size(max =50)
    @Email
    private String email;

    private String emailBeforeDeleted;

    @Size(max =256)
    @JsonIgnore
    private String guestInvitationLink;

    @NotBlank
    @Size(max =120)
    @JsonIgnore
    private String password;

    @Column
    private Boolean deleted;

    @Size(max =1024)
    @Column
    private String avatar;

   /* @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(fetch = FetchType.EAGER)
    private List<EntityExhibition> exhibitionsOnModerator;*/

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<EntityRole> role = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<EntitySessionPatient> sessionPatients;


    public EntityUser(@NotBlank @Size(max = 20) String fullname, @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 120) String password, @NotBlank @Size(max = 120) String gender, @NotBlank @Size(max = 120) String cellPhone, @NotBlank @Size(max = 120) Date birthdate) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.cellPhone = cellPhone;
        this.birthdate = birthdate;

    }
}
