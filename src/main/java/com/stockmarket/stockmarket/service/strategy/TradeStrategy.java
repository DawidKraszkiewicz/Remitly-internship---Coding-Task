package com.stockmarket.stockmarket.service.strategy;

public interface TradeStrategy {
    String getType();
    void execute(String walletId, String stockName);
}