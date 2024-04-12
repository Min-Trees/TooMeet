package com.group.groupsocial.command.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class ImageUploadConfig {
    private final String CLOUD_NAME = "ddybs8rud";
    private final String API_KEY = "268951499466626";
    private final String API_SECRET = "o7OPkXf0hBC81p2LKqoJ2Pgi77w";
    @Bean
    public Cloudinary cloudinary(){
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name",CLOUD_NAME);
        config.put("api_key",API_KEY);
        config.put("api_secret",API_SECRET);
        return new Cloudinary(config);
    }

}
