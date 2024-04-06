package com.group.groupsocial.command.listener.event;

import com.group.groupsocial.command.entity.User;

import java.util.UUID;

public class NewPostEvent {
    private UUID postId;
    private User postAuthor;
}
