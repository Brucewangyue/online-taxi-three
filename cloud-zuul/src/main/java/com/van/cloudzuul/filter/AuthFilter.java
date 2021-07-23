package com.van.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.van.internalcommon.constant.RedisKeyPrefixConstant;
import com.van.internalcommon.util.JwtInfo;
import com.van.internalcommon.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

public class AuthFilter extends ZuulFilter {
    @Resource
    StringRedisTemplate redisTemplate;

    @Override
    public String filterType() {
        // 在所有的过滤器之前
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("网关拦截");
        //获取上下文（重要，贯穿 所有filter，包含所有参数）
        RequestContext context = RequestContext.getCurrentContext();
        String token = context.getRequest().getHeader("Authorize");

        if (StringUtils.isNotBlank(token)) {
            JwtInfo jwtInfo = JwtUtil.parseToken(token);
            if (null != jwtInfo) {
                String redisToken = redisTemplate.opsForValue().get(RedisKeyPrefixConstant.PASSENGER_LOGIN_TOKEN_APP_KEY_PRE + jwtInfo.getSubject());
                if (redisToken.equals(token))
                    return null;
            }
        }

        // 还走剩下的过滤器，但不向后面的服务转发
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
        context.setResponseBody("unauthorized");
        return null;
    }
}
