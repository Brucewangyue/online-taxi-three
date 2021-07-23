package com.van.servicesms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.servicesms.request.SmsSendRequest;
import com.van.servicesms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("send")
public class SendController {
    private Logger logger = LoggerFactory.getLogger(SendController.class);
    private SmsService smsService;

    @Autowired
    public SendController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("sms-template")
    public ResponseResult send(@RequestBody SmsSendRequest request) throws JsonProcessingException {
        logger.info("sms-template requestBody:" + new ObjectMapper().writeValueAsString(request));
        return smsService.sendSms(request);
    }
}
