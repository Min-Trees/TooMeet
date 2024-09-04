package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class UserResponse {
    private UUID memberId;
    private User user;
    private String role;
    private UUID groupId;
    @CreationTimestamp
    private Date createAt;
}
