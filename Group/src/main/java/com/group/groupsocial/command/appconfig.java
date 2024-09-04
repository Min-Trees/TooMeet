package com.group.groupsocial.command;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class appconfig {

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

}
