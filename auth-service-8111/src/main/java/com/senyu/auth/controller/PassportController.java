package com.senyu.auth.controller;

import com.google.gson.Gson;
import com.senyu.QO.SMSContentQO;
import com.senyu.api.mq.RabbitMQSMSConfig;
import com.senyu.auth.service.UsersService;
import com.senyu.bo.RegisterLoginBO;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.GsonUtils;
import com.senyu.common.utils.IPUtil;
import com.senyu.common.utils.JWTUtils;
import com.senyu.common.utils.SMSUtil;
import com.senyu.pojo.Users;
import com.senyu.vo.UsersVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
// -1 -1 0 1
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseInfoProperties {

    private static final Logger log = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        if (StringUtils.isEmpty(mobile)) {
            return GraceJSONResult.error();
        }
        // 限制用户ip 只能在60s内获得一次验证码
        String ip = IPUtil.getRequestIp(request);
        redis.setnx60s(MOBILE_SMSCODE + ":" + ip, mobile);
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
//        try {
//            smsUtil.sendEmail(mobile, code);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }

        // 使用消息队列异步解耦发送短信
        SMSContentQO smsContentQO = new SMSContentQO();
        smsContentQO.setMobile(mobile);
        smsContentQO.setContent(code);

        MessagePostProcessor processor = new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("60000");
                return message;
            }
        };
        rabbitTemplate.convertAndSend(RabbitMQSMSConfig.SMS_EXCHANGE,
                                    RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN,
                                    GsonUtils.object2String(smsContentQO), processor);


        log.info("验证码为：{}", code);
        // 验证码存入redis
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    public GraceJSONResult getSMSCode(@Valid @RequestBody RegisterLoginBO registerLoginBO, HttpServletRequest request) {
        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();
        // 1. 从redis中获取验证码检验是否一致
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (smsCode == null || smsCode.isEmpty() || !smsCode.equals(redisCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        // 2. 根据mobile查询用户是否存在 如果不存在 需要先进行注册
        Users users = usersService.queryMobileIsExist(mobile);
        if (users == null) {
            users = usersService.createUsers(mobile);
        }
        // 3. 保存用户token 分布式会话到redis中
//        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID().toString();
//        redis.set(REDIS_USER_TOKEN + ":" + users.getId(), uToken);
        String jwtWithPrefix = jwtUtils.createJWTWithPrefix(new Gson().toJson(users),  TOKEN_USER_PREFIX);
        // 4. 用户注册登录后 删除redis中的短信验证码
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        // 5. 返回用户信息给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        usersVO.setUserToken(jwtWithPrefix);
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId) {
//        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }
}
