package com.appointmentManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="blog_posts")
public class EntityBlogPost extends AbstractAuditableEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String title;

    public String content;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    public EntityUser user;

    public boolean deleted;

    @JsonIgnore
    @OrderBy("id")
    @OneToMany(mappedBy = "post",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    Set<EntityBlogPostInteraction> entityBlogPostInteractions;
}
