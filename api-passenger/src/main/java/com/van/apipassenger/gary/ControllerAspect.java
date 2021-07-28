package com.van.apipassenger.gary;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

// aspect 方面
@Aspect
@Component
public class ControllerAspect {

    // 指定切入点
    @Pointcut("execution(* com.van.apipassenger.controller..*Controller.*(..))")
    private void setLoginUserId() {

    }

    // advice：应用切入点
    @Before("setLoginUserId()")
    public void before() {
        System.out.println("进入pointcut");

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String userId = requestAttributes.getRequest().getHeader("userId");

        // todo jdk1.7 hashmap攻击
        //jdk1.7 数组+链表
        //jdk1.8 数组+链表+红黑树  》8转红黑树   《6转链表
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);

        GrayParameter.set(paramMap);
    }
}
