package com.powernode.p2p.service;

import com.powernode.p2p.vo.InvestRange;

import java.util.List;
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
    void set(String key, Object value, long time, TimeUnit timeUnit);

    /**
     *获取redis中的字符串
     * @param key
     * @return
     */
    String get(String key);


    /**
     * 将投资金额存入redis
     * @param key
     * @param value
     * @param score
     */
    void set(String key,String value,Double score);

    /**
     * 获取投资金额排行榜
     * @param key
     * @param count
     * @return
     */
    List<InvestRange> get(String key, Long count);

    String generateId(String key, Integer uid);
}
