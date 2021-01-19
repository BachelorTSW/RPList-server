package com.swl.mod.rplist.scheduled;

import com.swl.mod.rplist.service.RoleplayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class IdleRoleplayersCleaner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoleplayerService roleplayerService;

    @Scheduled(initialDelayString = "#{${rplist.player.idleCheck.initialDelayMinutes} * 60 * 1000}",
            fixedRateString = "#{${rplist.player.idleCheck.checkEveryMinutes} * 60 * 1000}")
    public void cleanIdle() {
        roleplayerService.removeIdle();
    }

}
