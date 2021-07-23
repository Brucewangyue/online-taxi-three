package com.van.serviceverificationcode.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.internalcommon.dto.ResponseResult;
import com.van.internalcommon.dto.serviceverifycode.request.VerifyRequest;
import com.van.internalcommon.dto.serviceverifycode.response.VerifyCodeResponse;
import com.van.serviceverificationcode.service.VerifyCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("verifyCode")
public class VerifyCodeController {
    private final Logger logger = LoggerFactory.getLogger(VerifyCodeController.class);

    @Autowired
    VerifyCodeService verifyCodeService;

    @GetMapping("generate/{identity}/{phoneNumber}")
    public ResponseResult<?> generate(@PathVariable int identity, @PathVariable String phoneNumber) {
        logger.info("/generate/{identity}/{phoneNumber} ： 身份类型：" + identity + ",手机号：" + phoneNumber);

        String code = verifyCodeService.generate(identity, phoneNumber);
        VerifyCodeResponse verifyCodeResponse = new VerifyCodeResponse();
        verifyCodeResponse.setCode(code);
        return ResponseResult.success(verifyCodeResponse);
    }

    @PostMapping("verify")
    public ResponseResult<?> verify(@RequestBody VerifyRequest request) throws JsonProcessingException {
        logger.info("verify body:"+ new ObjectMapper().writeValueAsString(request));
        return verifyCodeService.verify(request.getIdentity(), request.getPhoneNumber(), request.getCode());
    }
}
