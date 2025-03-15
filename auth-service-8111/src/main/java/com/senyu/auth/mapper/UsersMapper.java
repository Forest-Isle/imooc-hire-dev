package com.senyu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyu.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
