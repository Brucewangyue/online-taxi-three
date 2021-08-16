package com.van.cloudhystrixturbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
// hystix 请求信息汇总
@EnableTurbine
@EnableHystrixDashboard
public class CloudHystrixTurbineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudHystrixTurbineApplication.class, args);
    }

}
