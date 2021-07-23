package com.van.apipassenger.service.servicerest.serviceuser;

import com.van.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceRest {
    @Resource
    RestTemplate restTemplate;

    public ResponseResult login(String phoneNumber){
        String url = "http://service-user/auth/login";
        Map<String,Object> params = new HashMap<>();
        params.put("phoneNumber",phoneNumber);

        return restTemplate.postForObject(url, params, ResponseResult.class);
    }
}
