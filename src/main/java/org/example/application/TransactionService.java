package org.example.application;

import org.example.domain.Transaction;
import org.example.domain.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Transaction create(BigDecimal amount, String description, String category,
                              TransactionType type, LocalDate occurredAt) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        BigDecimal normalizedAmount = amount.stripTrailingZeros();
        if (normalizedAmount.scale() > 2 || normalizedAmount.precision() - normalizedAmount.scale() > 10) {
            throw new IllegalArgumentException("O valor deve ter no máximo 10 dígitos inteiros e 2 decimais.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição da transação é obrigatória.");
        }
        if (description.trim().length() > 200) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 200 caracteres.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("A categoria da transação é obrigatória.");
        }
        if (category.trim().length() > 80) {
            throw new IllegalArgumentException("A categoria deve ter no máximo 80 caracteres.");
        }
        if (type == null) {
            throw new IllegalArgumentException("O tipo da transação é obrigatório.");
        }

        return repository.save(new Transaction(null, amount, description.trim(), category.trim(),
                type, occurredAt == null ? LocalDate.now() : occurredAt));
    }

    @Transactional(readOnly = true)
    public List<Transaction> list() {
        return repository.findAll();
    }
}
