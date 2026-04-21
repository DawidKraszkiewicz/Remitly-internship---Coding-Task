package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.LogEntry;
import com.stockmarket.stockmarket.dto.LogResponse;
import com.stockmarket.stockmarket.model.AuditLog;
import com.stockmarket.stockmarket.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String type, String walletId, String stockName) {
        AuditLog entry = new AuditLog(null, type, walletId, stockName, LocalDateTime.now());
        auditLogRepository.save(entry);
    }

    public LogResponse getLog() {
        List<LogEntry> entries = auditLogRepository.findAllByOrderByIdAsc().stream()
                .map(l -> new LogEntry(l.getType(), l.getWalletId(), l.getStockName()))
                .toList();
        return new LogResponse(entries);
    }
}