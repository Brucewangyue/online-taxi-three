package com.van.apipassenger.gary;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GrayParameter {
    private static final ThreadLocal<Map<String, Object>> requestParamsMap = new ThreadLocal();

    public static Map<String, Object> get() {
        return requestParamsMap.get();
    }

    public static void set(Map<String, Object> map) {
        GrayParameter.requestParamsMap.set(map);
    }
}
