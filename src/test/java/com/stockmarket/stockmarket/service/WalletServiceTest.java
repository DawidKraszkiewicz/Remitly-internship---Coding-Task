package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.WalletResponse;
import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.strategy.BuyStrategy;
import com.stockmarket.stockmarket.service.strategy.SellStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletStockRepository walletStockRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private BuyStrategy buyStrategy;

    @Mock
    private SellStrategy sellStrategy;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        when(buyStrategy.getType()).thenReturn("buy");
        when(sellStrategy.getType()).thenReturn("sell");
        walletService = new WalletService(
                List.of(buyStrategy, sellStrategy),
                walletStockRepository,
                auditService
        );
        walletService.init();
    }

    @Test
    void trade_shouldCallCorrectStrategy_andLog() {
        walletService.trade("wallet1", "AAPL", "buy");

        verify(buyStrategy).execute("wallet1", "AAPL");
        verify(sellStrategy, never()).execute(any(), any());
        verify(auditService).log("buy", "wallet1", "AAPL");
    }

    @Test
    void trade_shouldThrow_whenUnknownType() {
        assertThatThrownBy(() -> walletService.trade("wallet1", "AAPL", "unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown");

        verify(auditService, never()).log(any(), any(), any());
    }

    @Test
    void trade_shouldNotLog_whenStrategyThrows() {
        doThrow(new InsufficientStockException("No stock"))
                .when(buyStrategy).execute("wallet1", "AAPL");

        assertThatThrownBy(() -> walletService.trade("wallet1", "AAPL", "buy"))
                .isInstanceOf(InsufficientStockException.class);

        verify(auditService, never()).log(any(), any(), any());
    }

    @Test
    void getWallet_shouldReturnMappedStocks() {
        WalletStockId id1 = new WalletStockId("wallet1", "AAPL");
        WalletStockId id2 = new WalletStockId("wallet1", "GOOG");
        when(walletStockRepository.findByIdWalletId("wallet1")).thenReturn(List.of(
                new WalletStock(id1, 10),
                new WalletStock(id2, 5)
        ));

        WalletResponse result = walletService.getWallet("wallet1");

        assertThat(result.id()).isEqualTo("wallet1");
        assertThat(result.stocks()).hasSize(2);
        assertThat(result.stocks().get(0).name()).isEqualTo("AAPL");
        assertThat(result.stocks().get(0).quantity()).isEqualTo(10);
    }

    @Test
    void getWallet_shouldReturnEmptyStocks_whenWalletHasNoStocks() {
        when(walletStockRepository.findByIdWalletId("wallet1")).thenReturn(List.of());

        WalletResponse result = walletService.getWallet("wallet1");

        assertThat(result.id()).isEqualTo("wallet1");
        assertThat(result.stocks()).isEmpty();
    }

    @Test
    void getStockQuantity_shouldReturnQuantity_whenStockExists() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.of(new WalletStock(wsId, 7)));

        int result = walletService.getStockQuantity("wallet1", "AAPL");

        assertThat(result).isEqualTo(7);
    }

    @Test
    void getStockQuantity_shouldThrow_whenStockNotFound() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getStockQuantity("wallet1", "AAPL"))
                .isInstanceOf(StockNotFoundException.class);
    }
}