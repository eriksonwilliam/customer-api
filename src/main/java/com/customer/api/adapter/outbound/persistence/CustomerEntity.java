package com.customer.api.adapter.outbound.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cpf"),
        @UniqueConstraint(columnNames = "email")
})
class CustomerEntity {

    @Id
    private UUID id;
    private String name;
    private String cpf;
    private String email;
    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected CustomerEntity() {}

    CustomerEntity(UUID id, String name, String cpf, String email, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    void setName(String name) { this.name = name; }
    void setCpf(String cpf) { this.cpf = cpf; }
    void setEmail(String email) { this.email = email; }
    void setPhone(String phone) { this.phone = phone; }
    void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    void setActive(boolean active) { this.active = active; }
    void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    UUID getId() { return id; }
    String getName() { return name; }
    String getCpf() { return cpf; }
    String getEmail() { return email; }
    String getPhone() { return phone; }
    LocalDateTime getCreatedAt() { return createdAt; }
    LocalDateTime getUpdatedAt() { return updatedAt; }
    boolean isActive() { return active; }
    LocalDateTime getDeletedAt() { return deletedAt; }
}