package com.stockmarket.stockmarket.controller;

import com.stockmarket.stockmarket.dto.BankStockRequest;
import com.stockmarket.stockmarket.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final BankService bankService;

    @GetMapping("/stocks")
    public Map<String, Object> getStocks() {
        return Map.of("stocks", bankService.getAllStocks());
    }

    @PostMapping("/stocks")
    public ResponseEntity<Void> setStocks(@RequestBody BankStockRequest request) {
        bankService.setStocks(request.stocks());
        return ResponseEntity.ok().build();
    }
}

