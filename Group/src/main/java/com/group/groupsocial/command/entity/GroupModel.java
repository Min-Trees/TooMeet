package com.group.groupsocial.command.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import java.util.List;
import java.util.UUID;
@Entity
@Table
@Getter
@Setter
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID groupId;
    @Column(length = 255)
    private String name;
    @Column(length = 255)
    private String description;
    private Long admin;
    private String avatar;
    private Integer quantityMember = 0;
    private Integer privacy = 0;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL)
    List<MemberModel> memberList;
    public GroupModel() {
        this.groupId = UUID.randomUUID(); // Tạo một UUID ngẫu nhiên khi tạo mới một GroupModel
    }

}
