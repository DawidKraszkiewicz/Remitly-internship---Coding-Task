package com.stockmarket.stockmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChaosController {

    @Autowired
    private ApplicationContext context;

    @PostMapping("/chaos")
    public String chaos() {
        Thread shutdownThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        });
        shutdownThread.setContextClassLoader(getClass().getClassLoader());
        shutdownThread.start();
        
        return "Initiating graceful shutdown...";
    }
}
