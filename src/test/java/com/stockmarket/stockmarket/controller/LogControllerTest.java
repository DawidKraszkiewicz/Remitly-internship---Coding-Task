package com.stockmarket.stockmarket.controller;

import com.stockmarket.stockmarket.dto.LogEntry;
import com.stockmarket.stockmarket.dto.LogResponse;

import com.stockmarket.stockmarket.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditService auditService;

    @Test
    void getLog_shouldReturnAllEntries() throws Exception {
        when(auditService.getLog()).thenReturn(new LogResponse(List.of(
                new LogEntry("buy", "wallet1", "AAPL"),
                new LogEntry("sell", "wallet2", "GOOG")
        )));

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log[0].type").value("buy"))
                .andExpect(jsonPath("$.log[0].wallet_id").value("wallet1"))
                .andExpect(jsonPath("$.log[0].stock_name").value("AAPL"))
                .andExpect(jsonPath("$.log[1].type").value("sell"))
                .andExpect(jsonPath("$.log[1].wallet_id").value("wallet2"))
                .andExpect(jsonPath("$.log[1].stock_name").value("GOOG"));
    }

    @Test
    void getLog_shouldReturnEmptyLog_whenNoEntries() throws Exception {
        when(auditService.getLog()).thenReturn(new LogResponse(List.of()));

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log").isEmpty());
    }
}