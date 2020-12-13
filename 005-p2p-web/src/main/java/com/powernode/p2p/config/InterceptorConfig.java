package com.powernode.p2p.config;

import com.powernode.p2p.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/15
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    //需要将LoginInterceptor注入ioc容器，又需要获取对象进行配置，所以需要在此处注入
    @Bean
    LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**/my*/**","/**/realName");
//        //定义需要拦截的路径
//        String [] addPathPatterns = {
//                "/**"
//        };
//
//        //定义不需要拦截的路径
//        String [] excludePathPatterns = {
//                "/loan/page/register",
//                "/loan/page/checkPhone",
//                "/loan/page/regist",
//                "/loan/page/messageCode",
//                "/loan/page/login",
//                "/loan/page/loginSubmit",
//                "/index",
//                "/loan/loan",
//                "/loan/loanInfo"
//        };
//
//
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/image/");
    }
}
