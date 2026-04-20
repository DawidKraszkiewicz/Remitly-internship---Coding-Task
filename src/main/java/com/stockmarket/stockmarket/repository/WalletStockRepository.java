package com.stockmarket.stockmarket.repository;

import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {
    List<WalletStock> findByIdWalletId(String walletId);
}
