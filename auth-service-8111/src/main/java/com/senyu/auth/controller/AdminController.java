package com.senyu.auth.controller;

import com.google.gson.Gson;
import com.senyu.api.intercept.JWTCurrentUserInterceptor;
import com.senyu.auth.service.AdminService;
import com.senyu.bo.AdminBO;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.JWTUtils;
import com.senyu.pojo.Admin;
import com.senyu.vo.AdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("admin")
public class AdminController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody AdminBO adminBO) {
        // 执行登录判断用户是否存在
        boolean isExist = adminService.adminLogin(adminBO);
        if (!isExist) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }
        // 登录成功之后获得admin信息
        Admin adminInfo = adminService.getAdminInfo(adminBO);
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(adminInfo), TOKEN_ADMIN_PREFIX);


        return GraceJSONResult.ok(adminToken);
    }

    @GetMapping("info")
    public GraceJSONResult info() {
        Admin admin = JWTCurrentUserInterceptor.currentAdminUser.get();
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);
        return GraceJSONResult.ok(adminVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout() {
        return GraceJSONResult.ok();
    }
}
