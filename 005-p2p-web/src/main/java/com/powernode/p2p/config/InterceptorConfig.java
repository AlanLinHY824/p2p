package com.powernode.p2p.config;

import com.powernode.p2p.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/15
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor = loginInterceptor();
        registry.addInterceptor(loginInterceptor).addPathPatterns("/my*.html","realName.html","/toRecharge.html","/toRechargeBack.html");
    }
}
