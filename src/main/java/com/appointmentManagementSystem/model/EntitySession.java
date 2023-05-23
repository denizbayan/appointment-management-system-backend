package com.appointmentManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
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
    @Size(max = 20)
    private String name;

    @Column(nullable = false)
    private Integer sessionCount=1;

    @JsonIgnore
    @OneToMany(mappedBy = "session",fetch = FetchType.EAGER)
    Set<EntitySessionPatient> sessionPatient;

    private boolean deleted;
}
