package org.example.api;

import org.example.application.TransactionService;
import org.example.domain.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody TransactionRequest request) {
        return service.create(request.amount(), request.description(), request.category(),
                request.type(), request.occurredAt());
    }

    @GetMapping
    public List<Transaction> list() {
        return service.list();
    }
}
