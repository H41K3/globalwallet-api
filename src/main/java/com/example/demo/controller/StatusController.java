package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class StatusController {

    @GetMapping("/status")
    public String getSystemStatus() {
        return "System is up and running! Ready for the Swiss market.";
    }
}