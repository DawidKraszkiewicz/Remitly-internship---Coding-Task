package com.stockmarket.stockmarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStockId implements Serializable {

    @Column(name="wallet_id")
    private String walletId;

    @Column(name="stock_name")
    private String stockName;
}
