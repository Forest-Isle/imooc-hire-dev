package com.senyu.api;

import com.senyu.common.config.BeanConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.senyu.api")
@Import(BeanConfig.class)
@EnableFeignClients(basePackages = "com.senyu.api.feign")
public class GlobalConfig {

}
