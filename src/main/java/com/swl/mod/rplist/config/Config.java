package com.swl.mod.rplist.config;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.scheduled.IdleRoleplayersCleaner;
import com.swl.mod.rplist.service.RoleplayerService;
import com.swl.mod.rplist.service.impl.RoleplayerServiceImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Core configuration.
 */
@Configuration
@Import({MvcConfig.class})
@EntityScan(basePackageClasses = Roleplayer.class)
@ComponentScan(basePackageClasses = RoleplayerDao.class)
@EnableNeo4jRepositories(basePackageClasses = RoleplayerDao.class)
public class Config {

    @Bean
    public RoleplayerService roleplayerService() {
        return new RoleplayerServiceImpl();
    }

    @Bean
    public IdleRoleplayersCleaner idleRoleplayersCleaner() {
        return new IdleRoleplayersCleaner();
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
