package com.group.groupsocial.command.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class DescriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    private String content;
    @OneToMany
    private List<MemberModel> topMember;
    @CreationTimestamp
    private Date createAt;

}
