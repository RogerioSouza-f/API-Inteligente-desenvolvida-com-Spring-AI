package org.example.api;

import org.example.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull @DecimalMin(value = "0.00", inclusive = false, message = "deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal amount,
        @NotBlank @Size(max = 200) String description,
        @NotBlank @Size(max = 80) String category,
        @NotNull TransactionType type,
        LocalDate occurredAt) {
}
