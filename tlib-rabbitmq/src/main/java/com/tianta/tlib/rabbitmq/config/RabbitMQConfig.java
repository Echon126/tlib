package com.tianta.tlib.rabbitmq.config;

import com.tianta.tlib.rabbitmq.send.MQSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class RabbitMQConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MQSender mqSender(){
        return new MQSender(rabbitTemplate);
    }
}
