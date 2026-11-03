package org.example.application;

import org.example.domain.Transaction;
import org.example.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private final TransactionRepository repository = mock(TransactionRepository.class);
    private final TransactionService service = new TransactionService(repository);

    @Test
    void createsTransactionAndNormalizesText() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = service.create(new BigDecimal("12.50"), "  almoço ", " alimentação ",
                TransactionType.EXPENSE, LocalDate.of(2026, 11, 2));

        assertThat(result.description()).isEqualTo("almoço");
        assertThat(result.category()).isEqualTo("alimentação");
        assertThat(result.amount()).isEqualByComparingTo("12.50");
        verify(repository).save(result);
    }

    @Test
    void rejectsNonPositiveAmountBeforePersistence() {
        assertThatThrownBy(() -> service.create(BigDecimal.ZERO, "café", "alimentação",
                TransactionType.EXPENSE, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que zero");
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsAmountsThatExceedDatabasePrecision() {
        assertThatThrownBy(() -> service.create(new BigDecimal("10000000000.00"), "café",
                "alimentação", TransactionType.EXPENSE, LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10 dígitos inteiros");
        verifyNoInteractions(repository);
    }

    @Test
    void listsRepositoryTransactions() {
        List<Transaction> expected = List.of(new Transaction(1L, BigDecimal.TEN, "salário",
                "renda", TransactionType.INCOME, LocalDate.now()));
        when(repository.findAll()).thenReturn(expected);

        assertThat(service.list()).isEqualTo(expected);
    }
}
