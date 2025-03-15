package com.senyu.work.service.impl;

import com.senyu.pojo.Resume;
import com.senyu.work.mapper.ResumeMapper;
import com.senyu.work.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 简历表 服务实现类
 * </p>
 *
 * @author senyu
 * @since 2025-03-12
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);
    @Autowired
    private ResumeMapper resumeMapper;

    @Override
    @Transactional
    public void initResume(String userId) {
        Resume resume = new Resume();
        log.info("userId = " + userId);
        resume.setUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        resume.setCreateTime(now);
        resume.setUpdatedTime(now);

        resumeMapper.insert(resume);
    }
}
