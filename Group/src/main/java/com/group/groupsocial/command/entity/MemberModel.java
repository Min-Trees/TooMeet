package com.group.groupsocial.command.entity;
import com.group.groupsocial.command.entity.User;
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
    public enum Role {
        USER, ADMIN
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Column
    private Role role;
    private UUID groupId;
    @CreationTimestamp
    private Date createAt;
}
