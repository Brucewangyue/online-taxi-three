package com.van.apipassenger.service.servicerest;

import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.servicesms.SmsTemplateDto;
import com.van.internalcommon.dto.servicesms.request.SmsSendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SmsServiceRest {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送短信
     */
    public ResponseResult sendSms(String phoneNumber, String code) {
        String url = "http://SERVICE-SMS/send/sms-template";
        SmsSendRequest smsSendRequest = new SmsSendRequest();
        String[] phoneNumbers = new String[]{phoneNumber};
        smsSendRequest.setReceivers(phoneNumbers);

        List<SmsTemplateDto> data = new ArrayList<>();
        SmsTemplateDto dto = new SmsTemplateDto();
        dto.setId("SMS_144145499");
        HashMap<String, Object> templateMap = new HashMap<>(1);
        templateMap.put("code", code);
        dto.setParamsMap(templateMap);
        data.add(dto);

        smsSendRequest.setTemplates(data);

        return restTemplate.postForObject(url, smsSendRequest, ResponseResult.class);
    }
}
