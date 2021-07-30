package com.van.cloudzuul.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/routeMap.yaml")
@ConfigurationProperties(prefix = "routes")
@Data
public class RouteMapConfig {
    @Value("${name}")
    private String name;
}
