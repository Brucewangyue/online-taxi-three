package com.van.cloudzuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

//@Component
public class GrayFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

//    @Override
//    public boolean shouldFilter() {
//        RequestContext currentContext = RequestContext.getCurrentContext();
//        return currentContext.sendZuulResponse();
//    }


//    @Override
//    public Object run() throws ZuulException {
//        RequestContext currentContext = RequestContext.getCurrentContext();
//        String userId = currentContext.get("userId").toString();
//
//        // 根据登录用户id到数据库查询的灰度规则
//
//
//
//        return null;
//    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        String userId = currentContext.getRequest().getHeader("userId");

        // 根据登录用户id到数据库查询的灰度规则
        if (userId.equals("1"))
            RibbonFilterContextHolder.getCurrentContext().add("grayVersion", "v1");
        else
            RibbonFilterContextHolder.getCurrentContext().add("grayVersion", "v2");

        return null;
    }

}
