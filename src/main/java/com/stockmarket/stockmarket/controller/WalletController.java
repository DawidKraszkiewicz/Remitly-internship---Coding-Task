package com.stockmarket.stockmarket.controller;

import com.stockmarket.stockmarket.dto.TradeRequest;
import com.stockmarket.stockmarket.dto.WalletResponse;
import com.stockmarket.stockmarket.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallets/{walletId}/stocks/{stockName}")
    public ResponseEntity<Void> trade(
            @PathVariable String walletId,
            @PathVariable String stockName,
            @RequestBody TradeRequest request) {
        walletService.trade(walletId, stockName, request.type());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public WalletResponse getWallet(@PathVariable String walletId) {
        return walletService.getWallet(walletId);
    }

    @GetMapping("/wallets/{walletId}/stocks/{stockName}")
    public int getStock(@PathVariable String walletId, @PathVariable String stockName) {
        return walletService.getStockQuantity(walletId, stockName);
    }
}