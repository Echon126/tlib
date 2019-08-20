package com.tianta.tlib.redis.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import({RedisConfiguration.class})
public @interface EnableTRedis {
}
