package com.appointmentManagementSystem.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="dictionary")
public class EntityDictionaryWord extends AbstractAuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String word;

    public String meaning;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    public EntityUser user;

    public boolean deleted;
}
