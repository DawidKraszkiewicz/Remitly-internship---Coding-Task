package com.stockmarket.stockmarket.service;

import com.stockmarket.stockmarket.dto.LogResponse;
import com.stockmarket.stockmarket.model.AuditLog;
import com.stockmarket.stockmarket.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void log_shouldSaveAuditEntry() {
        auditService.log("buy", "wallet1", "AAPL");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo("buy");
        assertThat(captor.getValue().getWalletId()).isEqualTo("wallet1");
        assertThat(captor.getValue().getStockName()).isEqualTo("AAPL");
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void getLog_shouldReturnAllEntriesInOrder() {
        when(auditLogRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                new AuditLog(1L, "buy", "wallet1", "AAPL", LocalDateTime.now()),
                new AuditLog(2L, "sell", "wallet2", "GOOG", LocalDateTime.now())
        ));

        LogResponse result = auditService.getLog();

        assertThat(result.log()).hasSize(2);
        assertThat(result.log().get(0).type()).isEqualTo("buy");
        assertThat(result.log().get(1).type()).isEqualTo("sell");
    }

    @Test
    void getLog_shouldReturnEmptyList_whenNoEntries() {
        when(auditLogRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

        LogResponse result = auditService.getLog();

        assertThat(result.log()).isEmpty();
    }
}
