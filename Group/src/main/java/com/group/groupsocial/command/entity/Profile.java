package com.group.groupsocial.command.entity;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Profile {
    private Image avatar;
    private String description;
    private Format format;
}
