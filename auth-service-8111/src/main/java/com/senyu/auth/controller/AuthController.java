package com.senyu.auth.controller;

import com.senyu.api.intercept.JWTCurrentUserInterceptor;
import com.senyu.common.base.BaseInfoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
@EnableDiscoveryClient
public class AuthController extends BaseInfoProperties {

    @GetMapping("test")
    public String test(HttpServletRequest request) {
        log.info("user:{}", JWTCurrentUserInterceptor.currentUser.get());
        return "test success!!!";
    }

}
