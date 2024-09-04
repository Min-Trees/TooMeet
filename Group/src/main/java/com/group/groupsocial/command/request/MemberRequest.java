package com.group.groupsocial.command.request;

import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@Getter
@Setter
public class MemberRequest {

    private MemberModel.Role role;
    private UUID groupId;

}
