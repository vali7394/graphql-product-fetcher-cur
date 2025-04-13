package com.gpc.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class ProductSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductSearchApplication.class, args);
    }

    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("virtual-thread-");
        // Use platform threads instead of virtual threads
        executor.initialize();
        return executor;
    }
} 