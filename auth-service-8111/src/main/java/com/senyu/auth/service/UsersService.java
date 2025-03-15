package com.senyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyu.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
public interface UsersService extends IService<Users> {

    /**
     * 判断用户是否存在
     * @param mobile
     * @return
     */
    Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息 设置信息
     * @param mobile
     */
    Users createUsers(String mobile);

}
