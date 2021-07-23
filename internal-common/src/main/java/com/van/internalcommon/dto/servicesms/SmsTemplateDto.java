package com.van.internalcommon.dto.servicesms;

import java.util.Map;

public class SmsTemplateDto {
    private String id;
    private Map<String,Object> paramsMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }
}
