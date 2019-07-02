/**
 * 西安中科天塔科技股份有限公司
 * Copyright (c) 2018-2028, tianta.INC All Rights Reserved.
 */
package com.tianta.tlib.swagger.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 
 * <b>Description：</b> swagger配置 <br/>
 * <b>ClassName：</b> TSwaggerConfig <br/>
 * <b>@author：</b> jackyshang <br/>
 * <b>@date：</b> 2018年11月27日 下午3:00:52 <br/>
 * <b>@version: </b> <br/>
 */
@Configuration
public class TSwaggerConfig {
    @Value("${swagger.enable:true}")
    boolean enable;

    @Value("${swagger.title:API查看器}")
    String title;

    @Value("${swagger.description:API服务的说明，请在配置文件中说明服务的作用}")
    String description;

    @Value("${swagger.contact.name:jackyshang}")
    String contactName;

    @Value("${swagger.contact.url:www.tianta.com.cn}")
    String contactUrl;

    @Value("${swagger.contact.mail:shangjunjie@tiantacaas.com}")
    String contactMail;

    @Value("${swagger.version:0.0.0}")
    String version;

    public TSwaggerConfig() {
    }

    @Bean
    public Docket allApi() {
        if (!this.enable) {
            return (new Docket(DocumentationType.SWAGGER_2)).select().apis(RequestHandlerSelectors.none())
                    .paths(PathSelectors.none()).build();
        } else {
            ApiInfo apiInfo = (new ApiInfoBuilder()).title(this.title).description(this.description)
                    .contact(new Contact(this.contactName, this.contactUrl, this.contactMail)).version(this.version)
                    .build();
            ApiSelectorBuilder builder = (new Docket(DocumentationType.SWAGGER_2)).useDefaultResponseMessages(false)
                    .apiInfo(apiInfo).select();
            builder.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class));
          //全局请求头域
          List<Parameter> headerParams = new ArrayList<>();
          ParameterBuilder tokenParam = new ParameterBuilder();
          tokenParam.name("token").description("会话令牌").modelRef(new ModelRef("String")).parameterType("header").required(false).build();
          headerParams.add(tokenParam.build());
          Docket docket = builder.build();
          docket.globalOperationParameters(headerParams);
            return docket;
        }
    }

    @Bean
    public CorsFilter apiCrosFilter() {
        if (!this.enable) {
            return new CorsFilter(new UrlBasedCorsConfigurationSource());
        } else {
            UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addAllowedOrigin("*");
            urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
            return new CorsFilter(urlBasedCorsConfigurationSource);
        }
    }
}
