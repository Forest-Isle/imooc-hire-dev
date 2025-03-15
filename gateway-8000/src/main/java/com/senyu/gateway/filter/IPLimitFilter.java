package com.senyu.gateway.filter;

import com.google.gson.Gson;
import com.senyu.common.GraceJSONResult;
import com.senyu.common.ResponseStatusEnum;
import com.senyu.common.base.BaseInfoProperties;
import com.senyu.common.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;
    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${blackIP.continueCount}")
    private Integer continueCounts;
    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取当前的请求路径
        String path = exchange.getRequest().getURI().getPath();
        // 2. 获取所有的需要进行ip限流校验的url list
        List<String> ipLimitUrls = excludeUrlProperties.getIpLimitUrls();
        // 3. 进行校验
        if (ipLimitUrls != null && !ipLimitUrls.isEmpty()) {
            for (String url : ipLimitUrls) {
                if (antPathMatcher.matchStart(url, path)) {
                    // 如果匹配到 进行ip拦截
                    log.info("limit url : {}", url);
                    return doLimit(exchange, chain);
                }
            }
        }
        // 4. 默认直接放行
        return chain.filter(exchange);
    }

    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取当前请求的ip
        String ip = IPUtil.getIP(exchange.getRequest());
        // 2. 存入redis
        String normalIP = "gateway-ip:" + ip; // 正常的ip
        String limitIP = "gateway-ip-limit:" + ip; // 被限制的ip
        // 3. 校验
        long ttl = redis.ttl(limitIP);
        if (ttl > 0) {
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR);
        }
        // 在redis中获得累加次数
        long increment = redis.increment(normalIP, 1);
        if (increment == 1) {
            redis.expire(normalIP, timeInterval);
        }
        if (increment > continueCounts) {
            redis.set(limitIP, limitIP, limitTimes);
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR);
        }
        return chain.filter(exchange);
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
        return 1;
    }
}
