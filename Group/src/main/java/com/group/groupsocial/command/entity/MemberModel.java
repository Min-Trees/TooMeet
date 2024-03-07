package com.group.groupsocial.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class MemberModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID memberId;
    private String role;
    private UUID groupId;
    @CreationTimestamp
    private Date createAt;
}
