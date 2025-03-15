package com.senyu.api.feign;

import com.senyu.common.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("work-service")
public interface WorkMicroServiceFeign {

    @PostMapping("/resume/init")
    public GraceJSONResult init(@RequestParam("userId") String userId);
}
