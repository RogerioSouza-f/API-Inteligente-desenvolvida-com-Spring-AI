package org.example.infrastructure.persistence;

import org.example.application.TransactionRepository;
import org.example.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final SpringDataTransactionRepository repository;

    public JpaTransactionRepository(SpringDataTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity saved = repository.save(toEntity(transaction));
        return toDomain(saved);
    }

    @Override
    public List<Transaction> findAll() {
        return repository.findAll().stream().map(JpaTransactionRepository::toDomain).toList();
    }

    private static TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(transaction.id(), transaction.amount(), transaction.description(),
                transaction.category(), transaction.type(), transaction.occurredAt());
    }

    private static Transaction toDomain(TransactionEntity entity) {
        return new Transaction(entity.getId(), entity.getAmount(), entity.getDescription(),
                entity.getCategory(), entity.getType(), entity.getOccurredAt());
    }
}
