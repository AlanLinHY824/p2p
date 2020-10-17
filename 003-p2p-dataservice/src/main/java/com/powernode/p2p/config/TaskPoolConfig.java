package com.powernode.p2p.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/17
 */
@Configuration
@EnableAsync
public class TaskPoolConfig {

//    @Bean(name = "taskExecutor")
//    public ThreadPoolTaskExecutor taskExecutor(){
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(10);
//        taskExecutor.setMaxPoolSize(50);
//        taskExecutor.setQueueCapacity(200);
//        taskExecutor.setKeepAliveSeconds(60);
//        taskExecutor.setThreadNamePrefix("taskExecutor--");
//        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
//        taskExecutor.setAwaitTerminationSeconds(60);
//        return taskExecutor;
//    }
}
