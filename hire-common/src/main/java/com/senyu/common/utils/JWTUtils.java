package com.senyu.common.utils;

import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.exeptions.GraceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RefreshScope
public class JWTUtils {

    private final JWTProperties jwtProperties;

    @Value("${jwt.key}")
    public String JWT_KEY;

    public static final String at = "@";

    @Autowired
    public JWTUtils(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createJWTWithPrefix(String body, Long expire, String prefix) {
        if (expire == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return prefix + at + createJWT(body, expire);
    }

    public String createJWTWithPrefix(String body, String prefix) {
        return prefix + at + createJWT(body);
    }

    public String createJWT(String body) {
        return dealJWT(body, null);
    }

    public String createJWT(String body, Long expire) {
        if (expire == null) {
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return dealJWT(body, expire);
    }

    public String dealJWT(String body, Long expireTime) {
//        String key = jwtProperties.getKey();
        String key = JWT_KEY;
        log.info("key:{}", key);
        // 1. 对密钥进行base64编码
        String base64 = new BASE64Encoder().encode(key.getBytes());
        // 2. 对base64生成一个密钥对象
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        String jwt = null;
        if (expireTime != null) {
            jwt = generatorJWT(body, expireTime, secretKey);
        } else {
            jwt = generatorJWT(body, secretKey);
        }
        log.info("jwt is {}",jwt);
        return jwt;
    }

    public String generatorJWT(String body, SecretKey secretKey) {
        String myJWT = Jwts.builder()
                .setSubject(body) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行jwt的生成
                .compact(); // 压缩并且生成jwt
        return myJWT;
    }

    public String generatorJWT(String body, long expireTimes, SecretKey secretKey) {
        // 定义过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireTimes);
        String myJWT = Jwts.builder()
                .setSubject(body) // 设置用户自定义数据
                .signWith(secretKey) // 使用哪个密钥对象进行jwt的生成
                .setExpiration(expireDate)
                .compact(); // 压缩并且生成jwt
        return myJWT;
    }

    public String checkJWT(String jwt) {
//        String key = jwtProperties.getKey();
        String key = JWT_KEY;
        log.info("key:{}", key);
        String base64 = new BASE64Encoder().encode(key.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build(); // 构造解析器
        // 解析成功 可以获得Claims 从而去获取相关数据 如果跑异常 说明解析不通过
        Jws<Claims> claims = jwtParser.parseClaimsJws(jwt); // 解析jwt
        String body = claims.getBody().getSubject();
        return body;
    }
}
