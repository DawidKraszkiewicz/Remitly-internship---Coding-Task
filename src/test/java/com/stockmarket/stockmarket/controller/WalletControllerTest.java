package com.stockmarket.stockmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmarket.stockmarket.dto.StockDto;
import com.stockmarket.stockmarket.dto.TradeRequest;
import com.stockmarket.stockmarket.dto.WalletResponse;
import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private WalletService walletService;

    @Test
    void trade_shouldReturn200_whenBuySucceeds() throws Exception {
        mockMvc.perform(post("/wallets/wallet1/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
                .andExpect(status().isOk());

        verify(walletService).trade("wallet1", "AAPL", "buy");
    }

    @Test
    void trade_shouldReturn200_whenSellSucceeds() throws Exception {
        mockMvc.perform(post("/wallets/wallet1/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TradeRequest("sell"))))
                .andExpect(status().isOk());

        verify(walletService).trade("wallet1", "AAPL", "sell");
    }

    @Test
    void trade_shouldReturn404_whenStockNotFound() throws Exception {
        doThrow(new StockNotFoundException("Stock not found: AAPL"))
                .when(walletService).trade("wallet1", "AAPL", "buy");

        mockMvc.perform(post("/wallets/wallet1/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void trade_shouldReturn400_whenInsufficientStock() throws Exception {
        doThrow(new InsufficientStockException("No stock"))
                .when(walletService).trade("wallet1", "AAPL", "buy");

        mockMvc.perform(post("/wallets/wallet1/stocks/AAPL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TradeRequest("buy"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallet_shouldReturnWalletWithStocks() throws Exception {
        WalletResponse response = new WalletResponse("wallet1", List.of(
                new StockDto("AAPL", 10),
                new StockDto("GOOG", 5)
        ));
        when(walletService.getWallet("wallet1")).thenReturn(response);

        mockMvc.perform(get("/wallets/wallet1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet1"))
                .andExpect(jsonPath("$.stocks[0].name").value("AAPL"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(10))
                .andExpect(jsonPath("$.stocks[1].name").value("GOOG"))
                .andExpect(jsonPath("$.stocks[1].quantity").value(5));
    }

    @Test
    void getWallet_shouldReturnEmptyStocks_whenWalletHasNoStocks() throws Exception {
        when(walletService.getWallet("wallet1"))
                .thenReturn(new WalletResponse("wallet1", List.of()));

        mockMvc.perform(get("/wallets/wallet1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("wallet1"))
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void getStock_shouldReturnQuantity() throws Exception {
        when(walletService.getStockQuantity("wallet1", "AAPL")).thenReturn(7);

        mockMvc.perform(get("/wallets/wallet1/stocks/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    void getStock_shouldReturn404_whenStockNotFound() throws Exception {
        doThrow(new StockNotFoundException("Not found"))
                .when(walletService).getStockQuantity("wallet1", "AAPL");

        mockMvc.perform(get("/wallets/wallet1/stocks/AAPL"))
                .andExpect(status().isNotFound());
    }
}