package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/16
 */
@Service(interfaceClass = RedisService.class,version = "1.0.0",timeout = 20000)
@Component
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, String value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,time,timeUnit);
    }

    @Override
    public String get(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }
}
