package com.senyu.work.controller;

import com.senyu.common.GraceJSONResult;
import com.senyu.work.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("init")
    public GraceJSONResult init(@RequestParam("userId") String userId) {
        resumeService.initResume(userId);
        return GraceJSONResult.ok();
    }

}

