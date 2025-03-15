package com.senyu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyu.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 Mapper 接口
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}
