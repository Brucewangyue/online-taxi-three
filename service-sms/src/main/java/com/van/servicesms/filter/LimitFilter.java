package com.van.servicesms.filter;


import com.google.common.util.concurrent.RateLimiter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 限流
 */
public class LimitFilter implements Filter {

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(2);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (RATE_LIMITER.tryAcquire()) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        // todo response返回错误消息
    }

    @Override
    public void destroy() {

    }
}
