package com.swl.mod.rplist.config;

import com.swl.mod.rplist.controller.HealthController;
import com.swl.mod.rplist.controller.RobotsController;
import com.swl.mod.rplist.controller.VersionController;
import com.swl.mod.rplist.controller.RoleplayerController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of controllers.
 */
@Configuration
public class MvcConfig {

    @Bean
    public VersionController helloController() {
        return new VersionController();
    }

    @Bean
    public HealthController healthController() {
        return new HealthController();
    }

    @Bean
    public RoleplayerController roleplayerController() {
        return new RoleplayerController();
    }

    @Bean
    public RobotsController robotsController() {
        return new RobotsController();
    }

}
