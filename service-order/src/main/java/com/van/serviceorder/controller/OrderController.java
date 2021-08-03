package com.van.serviceorder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.van.internalcommon.dto.ResponseResult;
import com.van.serviceorder.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("create")
    public ResponseResult create() throws JsonProcessingException {
        ResponseResult responseResult = orderService.create();
        return responseResult;
    }
}
