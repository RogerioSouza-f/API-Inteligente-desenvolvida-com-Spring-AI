package org.example.infrastructure.persistence;

import org.example.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, length = 80)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDate occurredAt;

    protected TransactionEntity() {
    }

    public TransactionEntity(Long id, BigDecimal amount, String description, String category,
                             TransactionType type, LocalDate occurredAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public TransactionType getType() { return type; }
    public LocalDate getOccurredAt() { return occurredAt; }
}
