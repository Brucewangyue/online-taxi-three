package com.van.apipassenger.service.servicerest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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

    @HystrixCommand(fallbackMethod = "verifyFallback",commandProperties ={
//            @HystrixProperty(value = "",name = "")
    })
    public ResponseResult verify(String phoneNumber, String code) {
        System.out.println("正常逻辑");
        Map<String, Object> params = new HashMap<>();
        params.put("phoneNumber", phoneNumber);
        params.put("code", code);
        params.put("identity", IdentityConstant.PASSENGER);

        String url = "http://service-verification-code/verifyCode/verify";

        ResponseResult responseResult = restTemplate.postForObject(url, params, ResponseResult.class);
        return responseResult;
    }


    public ResponseResult verifyFallback(String phoneNumber, String code){
        System.out.println("熔断后降级");
        return ResponseResult.error("降级");
    }
}
