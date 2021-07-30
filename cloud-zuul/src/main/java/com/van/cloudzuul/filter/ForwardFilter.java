package com.van.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

//@Component
public class ForwardFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Object o = ctx.get(FilterConstants.SERVICE_ID_KEY);
        Object o1 = ctx.get(FilterConstants.REQUEST_URI_KEY);

        if (request.getRequestURI().contains("getPort1")) {
            ctx.set(FilterConstants.SERVICE_ID_KEY, "service-sms");
            ctx.set(FilterConstants.REQUEST_URI_KEY, "/test/getPort");
        }

        return null;
    }
}
