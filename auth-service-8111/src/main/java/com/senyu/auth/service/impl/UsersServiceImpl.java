package com.senyu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyu.api.feign.WorkMicroServiceFeign;
import com.senyu.auth.mapper.UsersMapper;
import com.senyu.auth.service.UsersService;
import com.senyu.common.enums.Sex;
import com.senyu.common.enums.ShowWhichName;
import com.senyu.common.enums.USER_DEFAULT_FACE;
import com.senyu.common.enums.UserRole;
import com.senyu.common.utils.DesensitizationUtil;
import com.senyu.common.utils.LocalDateUtils;
import com.senyu.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService{

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private WorkMicroServiceFeign workMicroServiceFeign;

    @Override
    public Users queryMobileIsExist(String mobile) {

        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getMobile, mobile);

        return usersMapper.selectOne(wrapper);

    }

    @Override
    @Transactional
    public Users createUsers(String mobile) {

        Users users = new Users();
        users.setMobile(mobile);
        users.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        users.setRealName("用户" + DesensitizationUtil.commonDisplay(mobile));
        users.setShowWhichName(ShowWhichName.nickname.type);
        users.setSex(Sex.secret.type);
        users.setFace(USER_DEFAULT_FACE.DEFAULT.url);
        users.setEmail("");
        LocalDate birthday = LocalDateUtils.parseLocalDate("1980-01-01", LocalDateUtils.DATE_PATTERN);
        users.setBirthday(birthday);
        users.setCountry("China");
        users.setProvince("");
        users.setCity("");
        users.setDistrict("");
        users.setDescription("这家伙很懒, 什么都没留下~");
        users.setStartWorkDate(LocalDate.now());
        users.setPosition("底层码农");
        users.setRole(UserRole.CANDIDATE.type);
        users.setHrInWhichCompanyId("");
        users.setCreatedTime(LocalDateTime.now());
        users.setUpdatedTime(LocalDateTime.now());
        usersMapper.insert(users);

        workMicroServiceFeign.init(users.getId());

        return users;
    }
}
