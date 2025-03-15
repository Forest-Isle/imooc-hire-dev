package com.senyu.api.intercept;

import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.exeptions.GraceException;
import com.senyu.common.utils.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SMSInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户ip
        String ip = IPUtil.getRequestIp(request);
        // 判断是否在60s内已经获取过验证码
        boolean isExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + ip);
        if (isExist) {
            log.error("短信发送过快，请稍后再试");
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
