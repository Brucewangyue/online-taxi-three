package com.van.cloudzuul.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

@Service
public class SentinelService {
    @SentinelResource(value = "SentinelService.success",blockHandler = "fallback")
    public String success(){
        System.out.println("sentinel service success");
        return "success";
    }

    public String fallback(BlockException e){
        System.out.println("sentinel service fallback");
        return "fallback";
    }
}
