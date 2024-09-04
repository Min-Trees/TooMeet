package com.group.groupsocial.command.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class GroupRequest {
    Long user_id;
    String name;
    MultipartFile avatar;
    String description;

}
