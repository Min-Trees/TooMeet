package com.group.groupsocial.command.mesage;
import com.group.groupsocial.command.entity.PostModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class PostMessage {
    private UUID postId;
    private UUID groupId;
    private Long userId;
    private String content;
    private List<String> images;
    private String status = String.valueOf(PostModel.Choice.accepted);
    @UpdateTimestamp
    private Date updatedAt;
    @CreationTimestamp
    private Date createAt;
}
