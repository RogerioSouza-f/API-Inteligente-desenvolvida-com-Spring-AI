package org.example.api;

import org.example.application.TransactionService;
import org.example.domain.Transaction;
import org.example.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(ApiExceptionHandler.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;

    @Test
    void createsAValidTransaction() throws Exception {
        when(service.create(any(), anyString(), anyString(), any(), any()))
                .thenReturn(new Transaction(1L, new BigDecimal("32.40"), "mercado",
                        "alimentação", TransactionType.EXPENSE, LocalDate.of(2026, 11, 2)));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":32.40,"description":"mercado","category":"alimentação",
                                 "type":"EXPENSE","occurredAt":"2026-11-02"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void rejectsInvalidTransactionWithExplicitDetails() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":0,"description":"","category":" ","type":"EXPENSE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
        verifyNoInteractions(service);
    }

    @Test
    void listsTransactions() throws Exception {
        when(service.list()).thenReturn(List.of(new Transaction(1L, BigDecimal.TEN,
                "almoço", "alimentação", TransactionType.EXPENSE, LocalDate.of(2026, 11, 2))));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[0].description").value("almoço"));
    }
}
