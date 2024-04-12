package com.group.groupsocial.command.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class AMQPConfig {
    @Value("${spring.rabbitmq.queue.group_new_post_queue}")
    String postNewPostQueue;
    @Value("${spring.rabbitmq.routing.group_new_post_routing}")
    String postNewPostRouting;
    @Value("${spring.rabbitmq.exchange.post_exchange}")
    String postExchange;

    @Bean
    public TopicExchange postExchange(){
        return new TopicExchange(postExchange);
    }
    @Bean
    public Queue postNewPostQueue(){
        return new Queue(postNewPostQueue);
    }
    @Bean
    public Binding postNewPostBinDing(){
        return BindingBuilder.bind(postNewPostQueue()).to(postExchange()).with(postNewPostRouting);
    }
    @Bean
    public AmqpTemplate amqpTemplate(final ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
