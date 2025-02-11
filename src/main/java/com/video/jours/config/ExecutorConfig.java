package com.video.jours.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean
    public BlockingQueue<Runnable> videoQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ThreadPoolExecutor videoExecutor() {
        return new ThreadPoolExecutor(
            2, // 코어 스레드 수
            2, // 최대 스레드 수
            0L, // 놀고 있는 스레드가 종료되기 전 대기 시간
            TimeUnit.MILLISECONDS,
            videoQueue() // 작업 큐
        );
    }
}
