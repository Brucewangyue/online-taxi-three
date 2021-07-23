package com.van.apipassenger.service;

import com.van.apipassenger.service.servicerest.VerifyCodeServiceRest;
import com.van.apipassenger.service.servicerest.serviceuser.AuthServiceRest;
import com.van.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthService {

    @Resource
    VerifyCodeServiceRest verifyCodeServiceRest;

    @Resource
    AuthServiceRest authServiceRest;

    public ResponseResult auth(String passengerPhone, String code) {
        // 校验验证码
        ResponseResult verifyResult = verifyCodeServiceRest.verify(passengerPhone, code);

        if (!verifyResult.isSuccess())
            return verifyResult;

        // 登录
        return authServiceRest.login(passengerPhone);
    }
}
