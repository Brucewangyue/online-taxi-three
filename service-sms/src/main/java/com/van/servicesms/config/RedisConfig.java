package com.van.servicesms.config;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RedisConfig {

    RedissonClient redissonClient;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress(":6379").setDatabase(0);
        RLock lock = redissonClient.getLock("");
        lock.lock();
        return Redisson.create(config);
    }
}
