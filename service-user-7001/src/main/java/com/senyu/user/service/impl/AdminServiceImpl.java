package com.senyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.senyu.bo.CreateAdminBO;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.exeptions.GraceException;
import com.senyu.common.utils.MD5Utils;
import com.senyu.common.utils.PagedGridResult;
import com.senyu.pojo.Admin;
import com.senyu.user.mapper.AdminMapper;
import com.senyu.user.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Transactional
    @Override
    public void createAdmin(CreateAdminBO createAdminBO) {
        // admin账号判断是否存在 如果存在 禁止账号分配
        Admin admin = getAdmin(createAdminBO.getUsername());
        if (admin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }

        Admin newAdmin = new Admin();
        BeanUtils.copyProperties(createAdminBO, newAdmin);
        // 生成随机数字或字母的盐
        String slat = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(createAdminBO.getPassword(), slat);
        newAdmin.setPassword(pwd);
        newAdmin.setSlat(slat);
        LocalDateTime now = LocalDateTime.now();
        newAdmin.setCreateTime(now);
        newAdmin.setUpdatedTime(now);

        adminMapper.insert(newAdmin);
    }

    @Override
    public PagedGridResult getAdminList(String accountName, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Admin> list = adminMapper.selectList(new LambdaQueryWrapper<Admin>().like(Admin::getUsername, accountName));
        return setterPagedGrid(list, page);
    }

    @Override
    public void deleteAdmin(String username) {
        int res = adminMapper.delete(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username).ne(Admin::getUsername, "admin"));
        if (res == 0) {
            GraceException.display(ResponseStatusEnum.ADMIN_DELETE_ERROR);
        }
    }

    private Admin getAdmin(String username) {
        return adminMapper.selectOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
    }
}
