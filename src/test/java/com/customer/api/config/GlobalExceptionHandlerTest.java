package com.customer.api.config;

import com.customer.api.domain.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    static class TestDomainException extends DomainException {
        public TestDomainException(String message) { super(message); }
    }

    @Test
    void handleDomainException_returnsBadRequest() {
        ProblemDetail p = handler.handleDomainException(new TestDomainException("msg"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), p.getStatus());
        assertEquals("Regra de negócio violada", p.getTitle());
        assertEquals("msg", p.getDetail());
    }

    @Test
    void handleNotFound_returnsNotFound() {
        ProblemDetail p = handler.handleNotFound(new CustomerNotFoundException(UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND.value(), p.getStatus());
        assertEquals("Cliente não encontrado", p.getTitle());
    }

    @Test
    void handleValidation_returnsBadRequest() {
        ProblemDetail p = handler.handleValidation(new InvalidCpfException("cpf"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), p.getStatus());
        assertEquals("Dados inválidos", p.getTitle());
    }

    @Test
    void handleDataIntegrity_email() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("email", new RuntimeException("uk_customer_email"));
        ProblemDetail p = handler.handleDataIntegrity(ex);
        assertEquals("Já existe cliente cadastrado com o email informado.", p.getDetail());
    }

    @Test
    void handleDataIntegrity_cpf() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("cpf", new RuntimeException("uk_customer_cpf"));
        ProblemDetail p = handler.handleDataIntegrity(ex);
        assertEquals("Já existe cliente cadastrado com o CPF informado.", p.getDetail());
    }

    @Test
    void handleDataIntegrity_other() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("other", new RuntimeException("other cause"));
        ProblemDetail p = handler.handleDataIntegrity(ex);
        assertEquals("Violação de integridade de dados.", p.getDetail());
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        ProblemDetail p = handler.handleIllegalArgument(new IllegalArgumentException("arg"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), p.getStatus());
        assertEquals("Argumento inválido", p.getTitle());
    }

    @Test
    void handleUnexpected_returnsInternalServerError() {
        ProblemDetail p = handler.handleUnexpected(new RuntimeException("fail"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), p.getStatus());
        assertEquals("Erro interno", p.getTitle());
    }
}
