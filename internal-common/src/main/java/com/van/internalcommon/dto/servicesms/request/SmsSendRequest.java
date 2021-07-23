package com.van.internalcommon.dto.servicesms.request;

import com.van.internalcommon.dto.servicesms.SmsTemplateDto;

import java.util.List;

public class SmsSendRequest {
    private String[] receivers;
    private List<SmsTemplateDto> templates;

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public List<SmsTemplateDto> getTemplates() {
        return templates;
    }

    public void setTemplates(List<SmsTemplateDto> templates) {
        this.templates = templates;
    }
}
