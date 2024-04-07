package com.group.groupsocial.command.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;

import java.util.UUID;
@Entity
@Table
@Getter
@Setter
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID groupId;
    @Column(length = 255)
    private String name;
    @Column(length = 255)
    private String description;
    private Long admin;
    private String avatar;
    private Integer quantityMember;
    private Integer privacy = 0;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

}
