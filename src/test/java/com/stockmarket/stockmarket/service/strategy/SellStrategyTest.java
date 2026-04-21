package com.stockmarket.stockmarket.service.strategy;

import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.model.WalletStock;
import com.stockmarket.stockmarket.model.WalletStockId;
import com.stockmarket.stockmarket.repository.WalletStockRepository;
import com.stockmarket.stockmarket.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellStrategyTest {

    @Mock
    private BankService bankService;

    @Mock
    private WalletStockRepository walletStockRepository;

    @InjectMocks
    private SellStrategy sellStrategy;

    @Test
    void getType_shouldReturnSell() {
        assertThat(sellStrategy.getType()).isEqualTo("sell");
    }

    @Test
    void execute_shouldDecrementWalletAndIncrementBank_whenStockAvailable() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        WalletStock ws = new WalletStock(wsId, 3);
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.of(ws));

        sellStrategy.execute("wallet1", "AAPL");

        assertThat(ws.getQuantity()).isEqualTo(2);
        verify(walletStockRepository).save(ws);
        verify(bankService).incrementStock("AAPL");
    }

    @Test
    void execute_shouldThrow_whenWalletStockDoesNotExist() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellStrategy.execute("wallet1", "AAPL"))
                .isInstanceOf(InsufficientStockException.class);

        verify(bankService, never()).incrementStock(any());
    }

    @Test
    void execute_shouldThrow_whenWalletStockQuantityIsZero() {
        WalletStockId wsId = new WalletStockId("wallet1", "AAPL");
        WalletStock ws = new WalletStock(wsId, 0);
        when(walletStockRepository.findById(wsId)).thenReturn(Optional.of(ws));

        assertThatThrownBy(() -> sellStrategy.execute("wallet1", "AAPL"))
                .isInstanceOf(InsufficientStockException.class);

        verify(bankService, never()).incrementStock(any());
    }
}