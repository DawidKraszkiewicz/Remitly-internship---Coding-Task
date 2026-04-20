package com.stockmarket.stockmarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStock {

    @Id
    @Column(name="stock_name")
    private String stockName;

    @Column(nullable = false)
    private int quantity;
}
