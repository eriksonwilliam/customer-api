package com.customer.api.domain;

import java.time.LocalDateTime;

public class Customer {

    private final CustomerId id;
    private String name;
    private Cpf cpf;
    private String email;
    private String phone;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true;
    private LocalDateTime deletedAt;

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
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, String cpf, String email, String phone) {
        this.name = name;
        this.cpf = new Cpf(cpf);
        this.email = email;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }

    public CustomerId id() { return id; }
    public String name() { return name; }
    public Cpf cpf() { return cpf; }
    public String email() { return email; }
    public String phone() { return phone; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime updatedAt() { return updatedAt; }
    public boolean active() { return active; }
    public LocalDateTime deletedAt() { return deletedAt; }
}