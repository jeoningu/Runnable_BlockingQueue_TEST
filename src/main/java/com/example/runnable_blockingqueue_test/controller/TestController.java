package com.example.runnable_blockingqueue_test.controller;


import com.example.runnable_blockingqueue_test.annotation.RequestLogAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/test")
    @RequestLogAnnotation
    public String test(HttpServletRequest request, String testValueA, String testValueB) {
        //logger.info("{}",request);
        //logger.info("{}",testValueA);
        //logger.info("{}",testValueB);
        return "1";
    }

}
