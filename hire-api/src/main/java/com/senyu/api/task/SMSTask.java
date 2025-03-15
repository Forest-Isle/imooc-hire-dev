package com.senyu.api.task;

import com.senyu.api.retry.RetryComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SMSTask {

    @Autowired
    private RetryComponent retryComponent;

    @Async
    public void sendSMSTask() {

    }
}
