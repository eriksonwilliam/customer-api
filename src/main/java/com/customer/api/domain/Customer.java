package com.customer.api.domain;

import com.customer.api.domain.exception.CustomerAlreadyExistsException;

import java.time.LocalDateTime;

public class Customer {

    private final CustomerId id;
    private final String name;
    private final Cpf cpf;
    private final String email;
    private final String phone;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Customer(CustomerId id, String name, Cpf cpf, String email, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String name, String email, String phone) {
        this.updatedAt = LocalDateTime.now();
    }

    public CustomerId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Cpf cpf() {
        return cpf;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }
}