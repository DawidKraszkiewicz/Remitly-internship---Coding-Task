package com.stockmarket.stockmarket.service.strategy;

import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyStrategyTest {

    @Mock
    private BankService bankService;

    @Mock
    private WalletStockRepository walletStockRepository;

    @InjectMocks
    private BuyStrategy buyStrategy;

    @Test
    void getType_shouldReturnBuy() {
        assertThat(buyStrategy.getType()).isEqualTo("buy");
    }

    @Test
    void execute_shouldDecrementBankAndCreateWalletEntry_whenWalletStockDoesNotExist() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.empty());

        buyStrategy.execute("wallet1", "AAPL");

        verify(bankService).decrementStock("AAPL");
        ArgumentCaptor<WalletStock> captor = ArgumentCaptor.forClass(WalletStock.class);
        verify(walletStockRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualTo(1);
    }

    @Test
    void execute_shouldDecrementBankAndIncrementWalletStock_whenWalletStockExists() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        WalletStock existing = new WalletStock(wsId, 3);
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.of(existing));

        buyStrategy.execute("wallet1", "AAPL");

        verify(bankService).decrementStock("AAPL");
        assertThat(existing.getQuantity()).isEqualTo(4);
        verify(walletStockRepository).save(existing);
    }

    @Test
    void execute_shouldThrow_whenBankHasNoStock() {
        doThrow(new InsufficientStockException("No stock"))
                .when(bankService).decrementStock("AAPL");

        assertThatThrownBy(() -> buyStrategy.execute("wallet1", "AAPL"))
                .isInstanceOf(InsufficientStockException.class);

        verify(walletStockRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrow_whenStockNotFoundInBank() {
        doThrow(new StockNotFoundException("Not found"))
                .when(bankService).decrementStock("AAPL");

        assertThatThrownBy(() -> buyStrategy.execute("wallet1", "AAPL"))
                .isInstanceOf(StockNotFoundException.class);

        verify(walletStockRepository, never()).save(any());
    }
}
