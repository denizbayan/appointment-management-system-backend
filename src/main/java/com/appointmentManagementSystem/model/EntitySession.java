package com.appointmentManagementSystem.model;

import com.appointmentManagementSystem.enums.EnumSessionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="session")
public class EntitySession extends AbstractAuditableEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false)
    private Integer sessionCount=1;

    @OneToOne
    @JoinColumn(name = "patientSession")
    public EntitySessionPatient patient_user;

    @OneToOne
    @JoinColumn(name = "psychologist_user")
    public EntityUser psychologist_user;

    private boolean deleted;

    private EnumSessionStatus status;


}
