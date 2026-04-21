package com.stockmarket.stockmarket.service.strategy;

import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.BankService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyStrategy implements TradeStrategy {

    private final BankService bankService;
    private final WalletStockRepository walletStockRepository;

    @Override
    public String getType() { return "buy"; }

    @Override
    @Transactional
    public void execute(String walletId, String stockName) {
        bankService.decrementStock(stockName);
        WalletStockId wsId = new WalletStockId(walletId, stockName);
        WalletStock ws = walletStockRepository.findById(wsId)
                .orElse(new WalletStock(wsId, 0));
        ws.setQuantity(ws.getQuantity() + 1);
        walletStockRepository.save(ws);
    }
}