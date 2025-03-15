package com.senyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyu.bo.AdminBO;
import com.senyu.pojo.Admin;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
public interface AdminService extends IService<Admin> {

    /**
     * admin 登录
     * @param adminBO
     * @return
     */
    boolean adminLogin(AdminBO adminBO);

    Admin getAdminInfo(AdminBO adminBO);
}
