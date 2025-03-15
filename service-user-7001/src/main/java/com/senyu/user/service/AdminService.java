package com.senyu.user.service;

import com.senyu.bo.CreateAdminBO;
import com.senyu.common.utils.PagedGridResult;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
public interface AdminService {

    /**
     * 分配admin账号
     * @param createAdminBO
     */
    void createAdmin(CreateAdminBO createAdminBO);

    /**
     * 查询admin列表
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    PagedGridResult getAdminList(String accountName, Integer page, Integer limit);

    void deleteAdmin(String username);
}
