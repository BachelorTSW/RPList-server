package com.swl.mod.rplist.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${info.build.version}")
    private String version;

    @RequestMapping("/version")
    public String hello() {
        return version;
    }

}
