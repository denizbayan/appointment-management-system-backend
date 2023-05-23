package com.appointmentManagementSystem.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="constants", uniqueConstraints = {
        @UniqueConstraint(columnNames = "key")
}
)
public class EntityConstant {

    @Id
    private String key;

    @Column
    private String value;

}
