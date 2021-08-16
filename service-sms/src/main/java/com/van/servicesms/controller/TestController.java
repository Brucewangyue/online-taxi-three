package com.van.servicesms.controller;

import com.van.servicesms.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @Value("${server.port}")
    String port;

    @GetMapping("getPort")
    public String getPort() {
        return port;
    }

    @Autowired
    @Qualifier("localLockService")
    LockService lockService;

    @GetMapping("lock")
    public String lock(int driverId) throws InterruptedException {
        lockService.robOrder(driverId);
        return "lock";
    }
}
