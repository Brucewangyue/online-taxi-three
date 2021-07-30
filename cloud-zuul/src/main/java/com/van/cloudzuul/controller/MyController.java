package com.van.cloudzuul.controller;

import com.van.cloudzuul.config.RouteMapConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @Autowired
    RouteMapConfig routeMapConfig;

    @GetMapping("/myController")
    public String myController(){
        return routeMapConfig.getName();
    }
}
