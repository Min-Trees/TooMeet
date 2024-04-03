package com.group.groupsocial.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class PostModel {
    public enum Choice {
        pending,
        accepted,
    }
    @Id
    private UUID postId;
    private UUID groupId;
    private UUID memberId;
    private Choice status = Choice.pending;
    @UpdateTimestamp
    private Date updatedAt;
}
