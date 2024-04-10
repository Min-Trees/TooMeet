package com.group.groupsocial.command.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class Image {
    private String url;
    private Format format;
    private Date createdAt;
    private Date updatedAt;

}
