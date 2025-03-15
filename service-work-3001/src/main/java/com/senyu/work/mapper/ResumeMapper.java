package com.senyu.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyu.pojo.Resume;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 简历表 Mapper 接口
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

}
