package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class MemberResponse {
    GroupResponse group;
    Date createAt;
    String role;
    User user;
}
