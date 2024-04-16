package com.group.groupsocial.command.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;
import java.io.Serial;
import java.io.Serializable;
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MemberId implements Serializable {

    private Long userId;
    @JsonIgnore
    private GroupModel group;

    public boolean isMemberOfGroup(GroupModel groupToCheck) {
        return group != null && Objects.equals(group, groupToCheck);
    }
    public MemberId withUserIdOrDefault(Long defaultValue) {
        if (userId != null) {
            return this;
        } else {
            MemberId defaultMemberId = new MemberId();
            defaultMemberId.setUserId(defaultValue);
            return defaultMemberId;
        }
    }

    public void save() {

    }
}
