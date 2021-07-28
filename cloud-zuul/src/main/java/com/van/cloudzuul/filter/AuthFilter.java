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
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

//@Component
public class AuthFilter extends ZuulFilter {
//    @Resource
//    StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        // 在所有的过滤器之前
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 值越小越先执行
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        // 判断上一个过滤器是否设置为已终止
        RequestContext context = RequestContext.getCurrentContext();
        return context.sendZuulResponse();
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
                String redisToken = "";// stringRedisTemplate.opsForValue().get(RedisKeyPrefixConstant.PASSENGER_LOGIN_TOKEN_APP_KEY_PRE + jwtInfo.getSubject());
                if (redisToken.equals(token)) {
                    context.set("userId", jwtInfo.getSubject());
                    return null;
                }
            }
        }

        // 还走剩下的过滤器，但不向后面的服务转发
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
        context.setResponseBody("unauthorized");
        return null;
    }

    public static void main(String[] args) {
        // 如果使用数字，小数只能对半拆分，0.5,0.25,0.125，否则只能无限靠近
        BigDecimal b1 = new BigDecimal(0.25);
        BigDecimal b2 = new BigDecimal(1);
        System.out.println(b1.add(b2));
        System.out.println(new BigDecimal(2.1));
    }
}
