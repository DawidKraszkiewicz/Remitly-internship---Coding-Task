package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.StockDto;
import com.stockmarket.stockmarket.dto.WalletResponse;
import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.strategy.TradeStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final List<TradeStrategy> strategies;
    private final WalletStockRepository walletStockRepository;
    private final AuditService auditService;

    private Map<String, TradeStrategy> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(TradeStrategy::getType, s -> s));
    }

    @Transactional
    public void trade(String walletId, String stockName, String type) {
        TradeStrategy strategy = Optional.ofNullable(strategyMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unknown trade type: " + type));

        strategy.execute(walletId, stockName);
        auditService.log(type, walletId, stockName);
    }

    public WalletResponse getWallet(String walletId) {
        List<StockDto> stocks = walletStockRepository.findByIdWalletId(walletId)
                .stream()
                .map(ws -> new StockDto(ws.getId().getStockName(), ws.getQuantity()))
                .toList();
        return new WalletResponse(walletId, stocks);
    }

    public int getStockQuantity(String walletId, String stockName) {
        return walletStockRepository.findById(new WalletStockId(walletId, stockName))
                .map(WalletStock::getQuantity)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found in wallet: " + stockName));
    }
}
