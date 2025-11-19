package com.customer.api.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDto(
        UUID id,
        String name,
        String cpf,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}