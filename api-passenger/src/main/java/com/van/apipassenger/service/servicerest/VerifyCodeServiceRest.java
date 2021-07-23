package com.van.apipassenger.service.servicerest;

import com.van.internalcommon.constant.IdentityConstant;
import com.van.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class VerifyCodeServiceRest {

    @Resource
    RestTemplate restTemplate;

    public ResponseResult verify(String phoneNumber, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);
        params.put("code", code);
        params.put("identity", IdentityConstant.PASSENGER);

        String url = "http://service-verification-code/verifyCode/verify";

        ResponseResult responseResult = restTemplate.postForObject(url, params, ResponseResult.class);
        return responseResult;
    }
}
