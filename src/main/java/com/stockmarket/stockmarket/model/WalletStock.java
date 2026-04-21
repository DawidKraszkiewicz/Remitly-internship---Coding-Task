package com.stockmarket.stockmarket.model;

import jakarta.persistence.*;
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