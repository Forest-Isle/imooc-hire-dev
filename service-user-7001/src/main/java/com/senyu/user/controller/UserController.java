package com.senyu.user.controller;

import com.senyu.user.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Value("${server.port}")
    private String port;

    @GetMapping("test")
    public String test() {
        return "hello I am user-service~~" + ",port:" + port + "~~";
    }
}
