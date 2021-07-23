package com.van.serviceuser.controller;

import com.van.internalcommon.dto.ResponseResult;
import com.van.serviceuser.model.auth.request.LoginRequest;
import com.van.serviceuser.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Resource
    AuthService authService;

    @PostMapping("login")
    public ResponseResult login(@RequestBody @Validated LoginRequest request){
        return authService.auth(request.getPhoneNumber());
    }

    @PostMapping("logout")
    public ResponseResult logout(String token){

        return ResponseResult.success("");
    }
}
