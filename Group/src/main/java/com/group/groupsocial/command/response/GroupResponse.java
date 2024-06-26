package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Component
public class GroupResponse {
    private UUID groupId;
    private String name;
    private String description;
    private User admin;
    private String avatar;
    private Integer quantityMember;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public void setUserId(Long userId) {
    }
    public static GroupResponse convert(GroupModel groupModel, User admin){
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(groupModel.getGroupId());
        groupResponse.setName(groupModel.getName());
        groupResponse.setAvatar(groupModel.getAvatar());
        groupResponse.setAdmin(admin);
        groupResponse.setDescription(groupModel.getDescription());
        groupResponse.setQuantityMember(groupModel.getQuantityMember());
        groupResponse.setCreatedAt(groupModel.getCreatedAt());
        groupResponse.setUpdatedAt(groupModel.getUpdatedAt());
        return groupResponse;
    }
}
