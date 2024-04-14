package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Component
public class PostResponse {
    private UUID postId;
    private UUID groupId;
    private Long memberId;
    private String content;
    private List<String> images;
    private String status;
    private User user;
    @UpdateTimestamp
    private Date updatedAt;
}
