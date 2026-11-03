package org.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        Long id,
        BigDecimal amount,
        String description,
        String category,
        TransactionType type,
        LocalDate occurredAt) {
}
