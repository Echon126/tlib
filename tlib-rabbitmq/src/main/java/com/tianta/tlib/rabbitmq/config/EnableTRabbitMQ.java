package com.tianta.tlib.rabbitmq.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import({RabbitMQConfig.class})
public @interface EnableTRabbitMQ {
}
