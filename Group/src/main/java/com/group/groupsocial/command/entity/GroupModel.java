package com.group.groupsocial.command.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.File;
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
    private String name;
    private String descriptionId;
    private UUID userId;
    private File avatar;
    private Integer quantityMember;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;


}
