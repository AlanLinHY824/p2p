package com.powernode.p2p.service;

import java.util.concurrent.TimeUnit;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/16
 */
public interface RedisService {
    /**
     * 将数据存储到redis
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    void set(String key, String value, long time, TimeUnit timeUnit);

    /**
     *
     * @param key
     * @return
     */
    String get(String key);
}
