package com.nextGenZeta.LoanApplicationSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class CustomThreadPool {


    private static final ThreadPoolTaskExecutor COMPUTATION_THREAD_POOL;
    private static final ThreadPoolTaskExecutor DB_THREAD_POOL;
    private static final ThreadPoolTaskExecutor HTTP_CLIENT_THREAD_POOL;


    static {
        COMPUTATION_THREAD_POOL = getExecutor("computationThread");
        DB_THREAD_POOL = getExecutor("databaseThread");
        HTTP_CLIENT_THREAD_POOL = getExecutor("httpThread");
    }


    private static ThreadPoolTaskExecutor getExecutor(String prefix) {

        ThreadPoolTaskExecutor customThreadPool = new ThreadPoolTaskExecutor();
        customThreadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        customThreadPool.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        customThreadPool.setKeepAliveSeconds(180);
        customThreadPool.setQueueCapacity(100);
        customThreadPool.setThreadNamePrefix(prefix);
        customThreadPool.initialize();
        return customThreadPool;

    }

    public static Executor getDatabaseExecutor() {
        return DB_THREAD_POOL;    }

    public static Executor getComputationExecutor() {
        return COMPUTATION_THREAD_POOL;    }


    public static Executor getHTTPExecutor() {
        return HTTP_CLIENT_THREAD_POOL;    }


}