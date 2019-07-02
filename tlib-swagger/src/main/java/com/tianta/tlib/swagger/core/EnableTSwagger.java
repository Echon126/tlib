/**
 * 西安中科天塔科技股份有限公司
 * Copyright (c) 2018-2028, tianta.INC All Rights Reserved.
 */
package com.tianta.tlib.swagger.core;

import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * Created by liuhuan on 2018/7/20 9:47.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@EnableSwagger2
@Import({TSwaggerConfig.class})
public @interface EnableTSwagger {
}
