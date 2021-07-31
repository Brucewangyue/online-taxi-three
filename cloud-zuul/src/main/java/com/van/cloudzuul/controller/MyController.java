package com.van.cloudzuul.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.van.cloudzuul.config.RouteMapConfig;
import com.van.cloudzuul.service.SentinelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @Autowired
    RouteMapConfig routeMapConfig;

    @Autowired
    SentinelService sentinelService;

    @GetMapping("/myController")
    public String myController(){
        return routeMapConfig.getName();
    }

    @GetMapping("/sentinel")
    public String sentinel(){
        return sentinelService.success();
    }
}
