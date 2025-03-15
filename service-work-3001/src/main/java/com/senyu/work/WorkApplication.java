package com.senyu.work;

import com.senyu.api.GlobalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(GlobalConfig.class)
public class WorkApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkApplication.class, args);
    }
}
