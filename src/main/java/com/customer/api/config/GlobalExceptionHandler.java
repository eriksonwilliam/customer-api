package com.customer.api.config;

import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.domain.exception.CustomerNotFoundException;
import com.customer.api.domain.exception.DomainException;
import com.customer.api.domain.exception.InvalidCpfException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomainException(DomainException ex) {ProblemDetail p=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);p.setTitle("Regra de negócio violada");p.setDetail(ex.getMessage());return p;}
    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleNotFound(CustomerNotFoundException ex){ProblemDetail p=ProblemDetail.forStatus(HttpStatus.NOT_FOUND);p.setTitle("Cliente não encontrado");p.setDetail(ex.getMessage());return p;}
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, InvalidCpfException.class})
    ProblemDetail handleValidation(Exception ex){ProblemDetail p=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);p.setTitle("Dados inválidos");p.setDetail(ex.getMessage());return p;}
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex){ProblemDetail p=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);p.setTitle("Regra de negócio violada");String msg=ex.getMostSpecificCause()!=null?ex.getMostSpecificCause().getMessage():ex.getMessage();if(msg!=null){String lower=msg.toLowerCase();if(lower.contains("uk_customer_email")||lower.contains("email")){p.setDetail("Já existe cliente cadastrado com o email informado.");}else if(lower.contains("uk_customer_cpf")||lower.contains("cpf")){p.setDetail("Já existe cliente cadastrado com o CPF informado.");}else{p.setDetail("Violação de integridade de dados.");}}else{p.setDetail("Violação de integridade de dados.");}return p;}
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex){ProblemDetail p=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);p.setTitle("Argumento inválido");p.setDetail(ex.getMessage());return p;}
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex){log.error("Unexpected error",ex);ProblemDetail p=ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);p.setTitle("Erro interno");p.setDetail("Ocorreu um erro inesperado. Tente novamente mais tarde.");return p;}
}