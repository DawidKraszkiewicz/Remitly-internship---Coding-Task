package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.StockDto;
import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.model.BankStock;
import com.stockmarket.stockmarket.repository.BankStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankStockRepository bankStockRepository;

    @Transactional
    public void setStocks(List<StockDto> stocks) {
        List<BankStock> entities = stocks.stream()
                .map(s -> new BankStock(s.name(), s.quantity()))
                .toList();
        bankStockRepository.saveAll(entities);
    }

    public List<StockDto> getAllStocks() {
        return bankStockRepository.findAll().stream()
                .map(s -> new StockDto(s.getStockName(), s.getQuantity()))
                .toList();
    }

    @Transactional
    public void decrementStock(String stockName) {
        BankStock stock = bankStockRepository.findById(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock not found: " + stockName));
        if (stock.getQuantity() == 0) {
            throw new InsufficientStockException("Bank has no stock: " + stockName);
        }
        stock.setQuantity(stock.getQuantity() - 1);
        bankStockRepository.save(stock);
    }

    @Transactional
    public void incrementStock(String stockName) {
        BankStock stock = bankStockRepository.findById(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock not found: " + stockName));
        stock.setQuantity(stock.getQuantity() + 1);
        bankStockRepository.save(stock);
    }
}