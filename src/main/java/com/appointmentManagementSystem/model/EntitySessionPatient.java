package com.appointmentManagementSystem.model;


import com.appointmentManagementSystem.enums.EnumVisitorStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "session_patient")
public class EntitySessionPatient extends AbstractAuditableEntity implements Serializable {


    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Long id;

    @OneToOne
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "session_id",referencedColumnName = "id")
    public EntitySession patientSession;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public EntityUser user;

    @JsonIgnore
    @OneToOne(mappedBy = "sessionPatient")
    private EntitySessionPatientInvitation invitation;

    @ToString.Include
    @Enumerated
    private EnumVisitorStatus status;

    @ToString.Include
    @Column(nullable = false)
    private boolean deleted;

}
