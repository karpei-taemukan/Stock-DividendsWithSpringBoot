package com.zerobase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // Thread Pool 생성
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();

        // Thread Pool 사이즈 설정
        threadPoolTaskScheduler.setPoolSize(
                // CPU 코어 갯수
                Runtime.getRuntime().availableProcessors() + 1
        );

        threadPoolTaskScheduler.initialize();

        // 스케쥴러에서 생성한 쓰레드 풀을 사용
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
