package com.powernode.p2p.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/21
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SpringSessionConfig {

    @Bean
    public DefaultCookieSerializer createDefaultCookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
//        //不同项目名称时添加
//        defaultCookieSerializer.setCookieName("/");
        //二级域名不同时添加
        defaultCookieSerializer.setDomainName("p2p.com");
        return defaultCookieSerializer;
    }

//    @Bean
//    public RedisHttpSessionConfiguration configuration(DefaultCookieSerializer createDefaultCookieSerializer){
//        RedisHttpSessionConfiguration redisHttpSessionConfiguration = new RedisHttpSessionConfiguration();
//        redisHttpSessionConfiguration.setMaxInactiveIntervalInSeconds(1800);
//        redisHttpSessionConfiguration.setCookieSerializer(createDefaultCookieSerializer);
//        return redisHttpSessionConfiguration;
//    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }


}
