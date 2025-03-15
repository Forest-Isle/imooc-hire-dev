package com.senyu.gateway.filter;

import com.google.gson.Gson;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;
    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final String HEADER_USER_TOKEN = "headerUserToken";

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取当前的请求路径
        String path = exchange.getRequest().getURI().getPath();
        // 2. 获取所有需要排除的路径list
        List<String> urls = excludeUrlProperties.getUrls();
        // 3. 进行校验
        if (urls != null && !urls.isEmpty()) {
            for (String url : urls) {
                if (antPathMatcher.matchStart(url, path)) {
                    // 如果匹配到 直接放行 表示当前的请求url是不需要被拦截校验的
                    return chain.filter(exchange);
                }
            }
        }
        log.info("被拦截了");

        // 判断header中是否有token， 对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);

        // 判空header中的令牌
        if (!StringUtils.isEmpty(userToken)) {
            String[] tokenArr = userToken.split(JWTUtils.at);
            if (tokenArr.length < 2) {
                return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);
            }
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)) {
                return dealJWT(jwt, exchange, APP_USER_JSON, chain);
            } else if (prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)) {
                return dealJWT(jwt, exchange, SAAS_USER_JSON, chain);
            } else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)) {
                return dealJWT(jwt, exchange, ADMIN_USER_JSON, chain);
            }
        }
        return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);
    }

    public Mono<Void> dealJWT(String jwt, ServerWebExchange exchange, String headerKey, GatewayFilterChain chain) {
        try {
            String userJson = jwtUtils.checkJWT(jwt);
            log.info("userJson:{}", userJson);
            ServerWebExchange newExchange = setNewHeader(exchange, headerKey, userJson);
            return chain.filter(newExchange);
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }
    }

    public ServerWebExchange setNewHeader(ServerWebExchange exchange, String headerKey, String headerValue) {
        // 重新构建新的request
        ServerHttpRequest newRequest = exchange.getRequest().mutate().header(headerKey, headerValue).build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return newExchange;
    }

    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {
        // 1. 获得response
        ServerHttpResponse response = exchange.getResponse();
        // 2. 构建jsonResult
        GraceJSONResult graceJSONResult = new GraceJSONResult(statusEnum);
        // 3. 修改response的code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 4. 设定header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        // 5. 转换json并且向response中写入数据
        String json = new Gson().toJson(graceJSONResult);
        DataBuffer dataBuffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
