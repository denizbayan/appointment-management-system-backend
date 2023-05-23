package com.appointmentManagementSystem.model;

import com.appointmentManagementSystem.enums.EnumInvitationStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name="session_patient_invitation")
public class EntitySessionPatientInvitation extends AbstractAuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_patient_id",referencedColumnName = "id")
    private EntitySessionPatient sessionPatient;

    @Column(nullable = false)
    private String link;

    @Column
    private String invitationCode;

    @Column
    private boolean deleted;

    @Lob
    private byte[] qrCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user")
    private EntityUser creatorUser;

    @Enumerated(EnumType.ORDINAL)
    private EnumInvitationStatus status;


}
