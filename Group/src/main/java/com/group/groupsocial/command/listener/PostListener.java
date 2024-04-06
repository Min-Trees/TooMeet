package com.group.groupsocial.command.listener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PostListener {
    @RabbitListener(queues = "q_post_newpost")
    public void newPostListener(String message){

    }

}
