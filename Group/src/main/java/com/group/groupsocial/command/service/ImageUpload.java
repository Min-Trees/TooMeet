package com.group.groupsocial.command.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUpload {
    String uploadImage(MultipartFile multipartFile) throws IOException;

}
