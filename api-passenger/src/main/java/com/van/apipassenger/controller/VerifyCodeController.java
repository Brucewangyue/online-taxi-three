package com.van.apipassenger.controller;

import com.van.apipassenger.request.verifycode.SendRequest;
import com.van.apipassenger.service.VerifyCodeService;
import com.van.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("verifyCode")
public class VerifyCodeController {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @PostMapping("send")
    public ResponseResult<?> send(@RequestBody @Validated SendRequest request) {
        return verifyCodeService.send(request.getPhoneNumber());
    }
}
