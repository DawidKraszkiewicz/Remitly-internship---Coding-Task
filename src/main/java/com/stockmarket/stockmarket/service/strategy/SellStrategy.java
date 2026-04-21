package com.stockmarket.stockmarket.service.strategy;

import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.BankService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellStrategy implements TradeStrategy {

    private final BankService bankService;
    private final WalletStockRepository walletStockRepository;

    @Override
    public String getType() { return "sell"; }

    @Override
    @Transactional
    public void execute(String walletId, String stockName) {
        WalletStockId wsId = new WalletStockId(walletId, stockName);
        WalletStock ws = walletStockRepository.findById(wsId)
                .orElseThrow(() -> new InsufficientStockException(
                        "Wallet has no stock: " + stockName));
        if (ws.getQuantity() == 0) {
            throw new InsufficientStockException("Wallet has no stock: " + stockName);
        }
        ws.setQuantity(ws.getQuantity() - 1);
        walletStockRepository.save(ws);
        bankService.incrementStock(stockName);
    }
}