package com.stockmarket.stockmarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStock {

    @EmbeddedId
    private WalletStockId id;

    @Column(nullable = false)
    private int quantity;
}