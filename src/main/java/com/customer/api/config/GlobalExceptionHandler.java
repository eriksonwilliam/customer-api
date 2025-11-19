package com.customer.api.config;

import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.domain.exception.CustomerNotFoundException;
import com.customer.api.domain.exception.DomainException;
import com.customer.api.domain.exception.InvalidCpfException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Regra de negócio violada");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleNotFound(CustomerNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Cliente não encontrado");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, InvalidCpfException.class})
    ProblemDetail handleValidation(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Dados inválidos");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Erro interno");
        problem.setDetail("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        return problem;
    }
}