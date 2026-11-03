package org.example.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
