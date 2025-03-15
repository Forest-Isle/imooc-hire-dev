package com.senyu.api.config;

import com.senyu.api.intercept.JWTCurrentUserInterceptor;
import com.senyu.api.intercept.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.senyu.api")
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    @Bean
    public JWTCurrentUserInterceptor jwtInterceptor() {
        return new JWTCurrentUserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor()).addPathPatterns("/passport/getSMSCode");
        registry.addInterceptor(jwtInterceptor()).addPathPatterns("/**");
    }
}
