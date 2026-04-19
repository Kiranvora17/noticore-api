package com.noticore.noticore_api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/api/v1/test")
    public void testApi() {
        log.info("api endpoint is working as expected");
    }
}
