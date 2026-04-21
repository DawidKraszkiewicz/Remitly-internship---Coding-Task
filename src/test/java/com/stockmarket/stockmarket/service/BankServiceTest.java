package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.StockDto;
import com.stockmarket.stockmarket.exception.InsufficientStockException;
import com.stockmarket.stockmarket.exception.StockNotFoundException;
import com.stockmarket.stockmarket.model.BankStock;
import com.stockmarket.stockmarket.repository.BankStockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankStockRepository bankStockRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    void setStocks_shouldSaveAllStocks() {
        List<StockDto> stocks = List.of(
                new StockDto("AAPL", 100),
                new StockDto("GOOG", 50)
        );

        bankService.setStocks(stocks);

        ArgumentCaptor<List<BankStock>> captor = ArgumentCaptor.forClass(List.class);
        verify(bankStockRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().getFirst().getStockName()).isEqualTo("AAPL");
    }

    @Test
    void getAllStocks_shouldReturnMappedDtos() {
        when(bankStockRepository.findAll()).thenReturn(List.of(
                new BankStock("AAPL", 100),
                new BankStock("GOOG", 50)
        ));

        List<StockDto> result = bankService.getAllStocks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("AAPL");
        assertThat(result.get(0).quantity()).isEqualTo(100);
    }

    @Test
    void decrementStock_shouldDecrementQuantityByOne() {
        BankStock stock = new BankStock("AAPL", 5);
        when(bankStockRepository.findById("AAPL")).thenReturn(Optional.of(stock));

        bankService.decrementStock("AAPL");

        verify(bankStockRepository).save(stock);
        assertThat(stock.getQuantity()).isEqualTo(4);
    }

    @Test
    void decrementStock_shouldThrowNotFound_whenStockDoesNotExist() {
        when(bankStockRepository.findById("AAPL")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.decrementStock("AAPL"))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessageContaining("AAPL");
    }

    @Test
    void decrementStock_shouldThrowInsufficientStock_whenQuantityIsZero() {
        BankStock stock = new BankStock("AAPL", 0);
        when(bankStockRepository.findById("AAPL")).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> bankService.decrementStock("AAPL"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("AAPL");
    }

    @Test
    void incrementStock_shouldIncrementQuantityByOne() {
        BankStock stock = new BankStock("AAPL", 5);
        when(bankStockRepository.findById("AAPL")).thenReturn(Optional.of(stock));

        bankService.incrementStock("AAPL");

        verify(bankStockRepository).save(stock);
        assertThat(stock.getQuantity()).isEqualTo(6);
    }

    @Test
    void incrementStock_shouldThrowNotFound_whenStockDoesNotExist() {
        when(bankStockRepository.findById("AAPL")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.incrementStock("AAPL"))
                .isInstanceOf(StockNotFoundException.class)
                .hasMessageContaining("AAPL");
    }
}