package com.appointmentManagementSystem.model;


import com.appointmentManagementSystem.enums.EnumVisitorStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "session_visitor")
public class EntitySessionPatient extends AbstractAuditableEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    public EntitySession session;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public EntityUser user;

    @JsonIgnore
    @OneToOne(mappedBy = "sessionPatient")
    private EntitySessionPatientInvitation invitation;

    @Enumerated(EnumType.ORDINAL)
    private EnumVisitorStatus status;

    private boolean deleted;

}
