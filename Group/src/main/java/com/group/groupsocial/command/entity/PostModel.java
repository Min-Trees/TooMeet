package com.group.groupsocial.command.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID postId;
    private UUID groupId;
    private Long memberId;
    private Long userId;
    private String content;
    @Builder.Default
    private List<String> images = new ArrayList<>();
    private Choice status = Choice.pending;
    @UpdateTimestamp
    private Date updatedAt;
    @CreationTimestamp
    private Date createAt;
}
