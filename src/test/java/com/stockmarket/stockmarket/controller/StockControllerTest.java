package com.stockmarket.stockmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmarket.stockmarket.dto.BankStockRequest;
import com.stockmarket.stockmarket.dto.StockDto;

import com.stockmarket.stockmarket.service.BankService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @MockitoBean
    private BankService bankService;

    @Test
    void getStocks_shouldReturnAllStocks() throws Exception {
        when(bankService.getAllStocks()).thenReturn(List.of(
                new StockDto("AAPL", 100),
                new StockDto("GOOG", 50)
        ));

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks[0].name").value("AAPL"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(100))
                .andExpect(jsonPath("$.stocks[1].name").value("GOOG"))
                .andExpect(jsonPath("$.stocks[1].quantity").value(50));
    }

    @Test
    void getStocks_shouldReturnEmptyList_whenBankIsEmpty() throws Exception {
        when(bankService.getAllStocks()).thenReturn(List.of());

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void setStocks_shouldReturn200_whenStocksSet() throws Exception {
        BankStockRequest request = new BankStockRequest(List.of(
                new StockDto("AAPL", 100),
                new StockDto("GOOG", 50)
        ));

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bankService).setStocks(request.stocks());
    }

    @Test
    void setStocks_shouldReturn200_whenEmptyListProvided() throws Exception {
        BankStockRequest request = new BankStockRequest(List.of());

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bankService).setStocks(List.of());
    }
}