package com.van.apipassenger.service;

import com.van.apipassenger.service.servicerest.SmsServiceRest;
import com.van.internalcommon.constant.IdentityConstant;
import com.van.internalcommon.constant.ResponseStatusEnum;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.serviceverifycode.response.VerifyCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VerifyCodeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SmsServiceRest smsServiceRest;

    public ResponseResult send(String phoneNumber) {
        // 调用service-verification-code 取验证码
        String url = String.format("http://service-verification-code/verifyCode/generate/{{0}}/{{1}}", IdentityConstant.PASSENGER, phoneNumber);
        ResponseEntity<ResponseResult> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, null), ResponseResult.class);
        ResponseResult<?> generateVerifyCodeResp = restTemplate.getForObject(url, ResponseResult.class);

        if (generateVerifyCodeResp == null || !generateVerifyCodeResp.isSuccess())
            return ResponseResult.error("获取验证码失败");

        VerifyCodeResponse verifyCodeResponse = generateVerifyCodeResp.parseDataToObject(VerifyCodeResponse.class);

        // 调用service-sms 发送短线
        ResponseResult sendSmsResp = smsServiceRest.sendSms(phoneNumber, verifyCodeResponse.getCode());
        if (sendSmsResp == null || sendSmsResp.getCode() != ResponseStatusEnum.SUCCESS.getCode())
            return ResponseResult.error("发送短信失败");

        return ResponseResult.success();
    }

}

