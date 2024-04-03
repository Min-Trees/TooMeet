package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
public class MemberResponse {
    UUID memberId;
    UUID groupId;
    Date createAt;
    String role;
    User user;
}
