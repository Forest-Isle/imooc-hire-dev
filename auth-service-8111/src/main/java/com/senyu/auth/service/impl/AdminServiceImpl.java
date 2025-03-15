package com.senyu.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyu.auth.mapper.AdminMapper;
import com.senyu.auth.service.AdminService;
import com.senyu.bo.AdminBO;
import com.senyu.common.utils.MD5Utils;
import com.senyu.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean adminLogin(AdminBO adminBO) {
        // 根据用户名获得salt值
        Admin admin = getAdminByUsername(adminBO.getUsername());
        if (admin == null) {
            return false;
        } else {
            String slat = admin.getSlat();
            String md5Pw = MD5Utils.encrypt(adminBO.getPassword(), slat);
            if (md5Pw.equalsIgnoreCase(admin.getPassword())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Admin getAdminInfo(AdminBO adminBO) {
        return getAdminByUsername(adminBO.getUsername());
    }

    private Admin getAdminByUsername(String username) {
        return lambdaQuery().eq(Admin::getUsername, username).one();
    }
}
