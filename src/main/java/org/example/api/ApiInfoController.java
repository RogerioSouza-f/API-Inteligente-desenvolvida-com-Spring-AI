package org.example.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiInfoController {
    @GetMapping("/")
    public Map<String, Object> info() {
        return Map.of(
                "name", "Voice Budget API",
                "message", "API de orçamento pessoal com suporte a transações por voz.",
                "endpoints", Map.of(
                        "transactions", "/api/transactions",
                        "audio", "/api/audio/transactions"));
    }
}
