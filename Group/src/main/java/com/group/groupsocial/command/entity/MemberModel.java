package com.group.groupsocial.command.entity;
import com.group.groupsocial.command.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@IdClass(MemberId.class)
public class MemberModel {
    public enum Role {
        USER, ADMIN
    }
    @Id
    private Long userId;
    @Column
    private Role role;
    @Id
    @ManyToOne
    private GroupModel group;
    @CreationTimestamp
    private Date createAt;

    // userId 2 role user
    // userId 2 role Admin

}
