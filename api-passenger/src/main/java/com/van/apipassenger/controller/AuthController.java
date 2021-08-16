package com.van.apipassenger.controller;

import com.van.apipassenger.request.auth.LoginRequest;
import com.van.apipassenger.service.AuthService;
import com.van.internalcommon.dto.ResponseResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Resource
    AuthService authService;

    @PostMapping("login")
    public ResponseResult login(@RequestBody @Validated LoginRequest request) {
        return authService.auth(request.getPassengerPhone(), request.getCode());
    }
}
