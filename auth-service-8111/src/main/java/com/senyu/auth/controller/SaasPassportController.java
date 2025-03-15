package com.senyu.auth.controller;

import com.google.gson.Gson;
import com.senyu.auth.service.UsersService;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.JWTUtils;
import com.senyu.pojo.Users;
import com.senyu.vo.SaasUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("saas")
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UsersService usersService;

    @PostMapping("getQRToken")
    public GraceJSONResult getQRToken() {
        // 生成扫码登陆的token
        String qrToken = UUID.randomUUID().toString();
        // 把qrToken存入到redis 设置一定失效 超时后需要刷新获得新的二维码
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5 * 60);
        // 存入redis标记当前的qrToken未被扫描
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5 * 60);
        // 返回给前端
        return GraceJSONResult.ok(qrToken);
    }

    @PostMapping("scanCode")
    public GraceJSONResult scanCode(String qrToken, HttpServletRequest request) {
        // 判空
        if (StringUtils.isEmpty(qrToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }

        String redisQRCode = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);
        if (!redisQRCode.equalsIgnoreCase(qrToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }
        // 从header中获得用户id和jwt令牌
        String headerAppUserId = request.getHeader("appUserId");
        String headerAppUserToken = request.getHeader("appUserToken");
        // 判空
        if (StringUtils.isEmpty(headerAppUserId) || StringUtils.isEmpty(headerAppUserToken)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        String jwt = jwtUtils.checkJWT(headerAppUserToken.split("@")[1]);
        if (StringUtils.isEmpty(jwt)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        // 执行后续正常业务
        // 生成预登录token
        String preToken = UUID.randomUUID().toString();
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, preToken, 5 * 60);
        // redis写入标记 当前qrToken需要被读取并且失效覆盖 网页端标记二维码已经被扫描
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "1," + preToken, 5 * 60);
        return GraceJSONResult.ok();
    }

    /**
     * SAAS网页端每隔一段时间（3秒）定时查询qrToken是否被读取 用于页面的展示标记判断
     * @param qrToken
     * @return
     */
    @PostMapping("codeHasBeenRead")
    public GraceJSONResult codeHasBeenRead(String qrToken) {
        String readStr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        List list = new ArrayList<>();
        if (!StringUtils.isEmpty(readStr)) {
            String[] readArr = readStr.split(",");
            if (readArr.length >= 2) {
                list.add(readArr[0]);
                list.add(readArr[1]);
            } else {
                list.add(0);
            }
            return GraceJSONResult.ok(list);
        } else {
            return GraceJSONResult.ok(list);
        }
    }

    @PostMapping("goQRLogin")
    public GraceJSONResult goQRLogin(String userId, String qrToken, String preToken) {
        String redisPreToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);
        if (!StringUtils.isEmpty(redisPreToken)) {
            if (preToken.equals(redisPreToken)) {
                // 根据用户id获得用户信息
                Users hrUser = usersService.getById(userId);
                if (hrUser == null) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
                }
                redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken, new Gson().toJson(hrUser), 5 * 60);
            }
        }
        return GraceJSONResult.ok();
    }

    @PostMapping("checkLogin")
    public GraceJSONResult checkLogin(String preToken) {
        if (StringUtils.isEmpty(preToken)) {
            return GraceJSONResult.ok("");
        }
        String hrUserInfo = redis.get(REDIS_SAAS_USER_INFO + ":temp:" + preToken);
        if (StringUtils.isEmpty(hrUserInfo)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        String jwtWithPrefix = jwtUtils.createJWTWithPrefix(hrUserInfo, TOKEN_SAAS_PREFIX);
        // 存入用户信息 长期有效
        redis.set(REDIS_SAAS_USER_INFO + ":" + jwtWithPrefix, hrUserInfo);

        return GraceJSONResult.ok(jwtWithPrefix);
    }

    @PostMapping("info")
    public GraceJSONResult info(String jwtToken) {
        String userInfo = redis.get(REDIS_SAAS_USER_INFO + ":" + jwtToken);
        Users user = new Gson().fromJson(userInfo, Users.class);
        SaasUserVO saasUserVO = new SaasUserVO();
        BeanUtils.copyProperties(user, saasUserVO);
        return GraceJSONResult.ok(saasUserVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(String jwtToken) {
        return GraceJSONResult.ok();
    }
}
