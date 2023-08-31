package com.appointmentManagementSystem.model;

import com.appointmentManagementSystem.enums.EnumBlogInteractionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="blog_post_interaction")
public class EntityBlogPostInteraction extends AbstractAuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private EntityBlogPost post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public EntityUser user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EnumBlogInteractionType interactionType;

    @Column(length = 256)
    private String interactionValue;

    private Boolean deleted;

}
