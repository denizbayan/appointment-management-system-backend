package com.appointmentManagementSystem.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractAuditableEntity {

    @CreatedBy
    private String  createdBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedBy
    private String  lastModifiedBy;

    @LastModifiedDate
    private Date lastModifiedDate;

}
