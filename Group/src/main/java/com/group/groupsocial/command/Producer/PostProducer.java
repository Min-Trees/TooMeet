package com.group.groupsocial.command.Producer;

import com.group.groupsocial.command.mesage.PostMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void sendUpdatePostStatusMessage(PostMessage postMessage) {
        rabbitTemplate.convertAndSend("q_post_update", "post.update", postMessage);
    }
}
