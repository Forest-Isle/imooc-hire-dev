package com.senyu.user.controller;

import com.senyu.bo.CreateAdminBO;
import com.senyu.bo.ResetPwdBO;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.JWTUtils;
import com.senyu.common.utils.PagedGridResult;
import com.senyu.user.mapper.AdminMapper;
import com.senyu.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("create")
    public GraceJSONResult create(@Valid @RequestBody CreateAdminBO adminBO) {
        adminService.createAdmin(adminBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(String accountName, Integer page, Integer limit) {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        PagedGridResult adminList = adminService.getAdminList(accountName, page, limit);
        return GraceJSONResult.ok(adminList);
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String accountName) {
        adminService.deleteAdmin(accountName);
        return GraceJSONResult.ok();
    }

    @PostMapping("resetPwd")
    public GraceJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO) {
        resetPwdBO.modifyPwd();
        return GraceJSONResult.ok();
    }

}
