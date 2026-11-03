package org.example.application;

import org.example.domain.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAll();
}
