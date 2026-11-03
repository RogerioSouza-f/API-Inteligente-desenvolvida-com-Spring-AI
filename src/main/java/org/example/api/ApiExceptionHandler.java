package org.example.api;

import org.example.infrastructure.ai.AudioConfigurationException;
import org.example.infrastructure.ai.AudioProviderException;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> invalidRequest(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(Exception exception) {
        String message = exception instanceof IllegalArgumentException
                ? exception.getMessage() : "Corpo da requisição inválido ou tipo de transação desconhecido.";
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AudioConfigurationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> audioConfiguration(AudioConfigurationException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(AudioProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, Object> audioProvider(AudioProviderException exception) {
        return error(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler({NonTransientAiException.class, TransientAiException.class})
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, Object> aiProvider(Exception exception) {
        return error(HttpStatus.BAD_GATEWAY, "Falha ao consultar o provedor de IA.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(NoResourceFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "O recurso solicitado não foi encontrado.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> unexpected(Exception exception) {
        logger.error("Erro inesperado ao processar a requisição.", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível concluir a operação.");
    }

    private Map<String, Object> error(HttpStatus status, String message) {
        return Map.of("timestamp", Instant.now(), "status", status.value(),
                "error", status.getReasonPhrase(), "message", message);
    }
}
