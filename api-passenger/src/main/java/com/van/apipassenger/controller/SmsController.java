package com.van.apipassenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("sms")
public class SmsController {
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("testLock")
    public String testLock(int driverId) {
        String forObject = restTemplate.getForObject("http://service-sms/test/lock?driverId=" + driverId, String.class);
        return "success";
    }
}
