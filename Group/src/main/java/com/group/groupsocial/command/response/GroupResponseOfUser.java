package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.GroupModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Component
public class GroupResponseOfUser {
    UUID groupId;
    String name;
    List<String> avatar;
    public static GroupResponseOfUser convert(GroupModel groupModel ,Long userId){
        GroupResponseOfUser groupResponse = new GroupResponseOfUser();
        groupResponse.setGroupId(groupModel.getGroupId());
        groupResponse.setName(groupModel.getName());
        groupResponse.setAvatar(Collections.singletonList(groupModel.getAvatar()));
        return groupResponse;
    }

}
