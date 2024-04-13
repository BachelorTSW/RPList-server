package com.swl.mod.rplist.controller;

import com.swl.mod.rplist.service.RoleplayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private RoleplayerService roleplayerService;

    @RequestMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        if (roleplayerService.getAll(true) != null) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.internalServerError().body("Error");
    }
}
