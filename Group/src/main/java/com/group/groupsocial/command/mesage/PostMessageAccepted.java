package com.group.groupsocial.command.mesage;

import com.group.groupsocial.command.entity.PostModel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PostMessageAccepted {
    private UUID postId;
    private UUID groupId;
}
