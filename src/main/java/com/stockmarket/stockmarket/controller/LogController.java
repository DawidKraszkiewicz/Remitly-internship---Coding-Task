package com.stockmarket.stockmarket.controller;

import com.stockmarket.stockmarket.dto.LogResponse;
import com.stockmarket.stockmarket.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogController {

    private final AuditService auditService;

    @GetMapping("/log")
    public LogResponse getLog() {
        return auditService.getLog();
    }
}