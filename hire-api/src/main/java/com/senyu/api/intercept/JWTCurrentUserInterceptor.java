package com.senyu.api.intercept;

import com.google.gson.Gson;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.pojo.Admin;
import com.senyu.pojo.Users;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTCurrentUserInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    public static ThreadLocal<Users> currentUser = new ThreadLocal<>();
    public static ThreadLocal<Admin> currentAdminUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appUserJson = request.getHeader(APP_USER_JSON);
        String sassUserJson = request.getHeader(SAAS_USER_JSON);
        String adminUserJson = request.getHeader(ADMIN_USER_JSON);
        if (!StringUtils.isEmpty(appUserJson) || !StringUtils.isEmpty(sassUserJson)) {
            Users appUser = new Gson().fromJson(appUserJson, Users.class);
            currentUser.set(appUser);
        }else if (!StringUtils.isEmpty(adminUserJson)) {
            Admin adminUser = new Gson().fromJson(adminUserJson, Admin.class);
            currentAdminUser.set(adminUser);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        currentAdminUser.remove();
        currentUser.remove();
    }
}
