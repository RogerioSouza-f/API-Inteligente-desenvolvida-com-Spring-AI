package org.example.application;

import org.example.domain.Transaction;
import org.example.domain.TransactionType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionTools {
    private final TransactionService transactions;

    public TransactionTools(TransactionService transactions) {
        this.transactions = transactions;
    }

    @Tool(description = "Registra uma receita ou despesa somente quando o usuário solicitar explicitamente.")
    public Transaction createTransaction(
            @ToolParam(description = "Valor positivo em reais") BigDecimal amount,
            @ToolParam(description = "Descrição curta da transação") String description,
            @ToolParam(description = "Categoria, por exemplo alimentação ou transporte") String category,
            @ToolParam(description = "INCOME para receita ou EXPENSE para despesa") String type,
            @ToolParam(required = false,
                    description = "Data opcional no formato ISO yyyy-MM-dd; usa a data atual se omitida") String occurredAt) {
        return transactions.create(amount, description, category, TransactionType.valueOf(type.toUpperCase()),
                occurredAt == null || occurredAt.isBlank() ? LocalDate.now() : LocalDate.parse(occurredAt));
    }

    @Tool(description = "Lista todas as receitas e despesas já registradas.")
    public List<Transaction> listTransactions() {
        return transactions.list();
    }
}
