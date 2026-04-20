package com.stockmarket.stockmarket.repository;

import com.stockmarket.stockmarket.model.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankStockRepository extends JpaRepository<BankStock, String> {}
