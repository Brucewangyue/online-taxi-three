package com.van.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

//@Component
public class HostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String userId = request.getParameter("userId");

        try {
            if (userId.equals("1")) {
                ctx.setRouteHost(new URI("https://baidu.com").toURL());
            } else {
                ctx.setRouteHost(new URI("https://www.bilibili.com/").toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
