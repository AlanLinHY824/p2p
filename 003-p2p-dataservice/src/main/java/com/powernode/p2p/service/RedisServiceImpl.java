package com.powernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.vo.InvestRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
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
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,time,timeUnit);
    }

    @Override
    public String get(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value, Double score) {
        redisTemplate.opsForZSet().incrementScore(MyConstants.INVEST_RANGE, value,score);
    }

    @Override
    public List<InvestRange> get(String key, Long count) {
        List<InvestRange> investRangeList=new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, count);
        for (ZSetOperations.TypedTuple<Object> typedTuple : typedTuples) {
            investRangeList.add(new InvestRange((String)typedTuple.getValue(),typedTuple.getScore()));
        }
        return investRangeList;
    }

    @Override
    public String generateId(String key, Integer uid) {
        String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(MyConstants.ORDERSEQUENCE,redisTemplate.getConnectionFactory());
        redisAtomicLong.expireAt(todayEnd.getTime());
        long num = redisAtomicLong.incrementAndGet();
        return dateString+uid+String.format("%0" + (10-uid.toString().length()) + "d", num);
    }
}
