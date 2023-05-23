package com.appointmentManagementSystem.model;

import com.appointmentManagementSystem.enums.EnumRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="roles")
public class EntityRole extends AbstractAuditableEntity{



        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Enumerated(EnumType.STRING)
        @Column(length = 20)
        private EnumRole roleName;








}
