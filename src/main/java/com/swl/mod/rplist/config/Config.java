package com.swl.mod.rplist.config;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.service.RoleplayerService;
import com.swl.mod.rplist.service.impl.RoleplayerServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Core configuration.
 */
@Configuration
@Import({MvcConfig.class})
@ComponentScan(basePackageClasses = RoleplayerDao.class)
@EnableRedisRepositories(basePackageClasses = RoleplayerDao.class)
public class Config {

    @Bean
    public RoleplayerService roleplayerService() {
        return new RoleplayerServiceImpl();
    }

    @Bean
    @Primary
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(2);
        return threadPoolTaskExecutor;
    }

    @Bean
    public ThreadPoolTaskExecutor generalThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("Exec-");
        return executor;
    }

}
