package com.group.groupsocial.command.service;

import com.group.groupsocial.command.mesage.PostMessage;
import com.group.groupsocial.command.mesage.PostMessageAccepted;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AMQPService {
    @Value("${spring.rabbitmq.queue.post_new_post_queue}")
    String postNewPostQueue;
    @Value("${spring.rabbitmq.routing.post_new_post_routing}")
    String postNewPostRouting;
    @Value("${spring.rabbitmq.queue.post_update_post_queue}")
    String postUpdatePostQueue;
    @Value("${spring.rabbitmq.routing.post_new_update_routing}")
    String postUpdatePostRouting;
    @Value("${spring.rabbitmq.exchange.post_exchange}")
    String postExchange;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    public AMQPService(AmqpTemplate amqpTemplate){
        this.amqpTemplate = amqpTemplate;
    }

    public void sendMessage(PostMessage postMessage){
        amqpTemplate.convertAndSend(postExchange,postNewPostRouting,postMessage);
    }

    public void sendMessageAccepted(PostMessageAccepted postMessage){
        amqpTemplate.convertAndSend(postExchange,postUpdatePostRouting,postMessage);
    }
}
