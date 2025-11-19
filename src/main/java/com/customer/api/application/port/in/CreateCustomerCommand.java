package com.customer.api.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerCommand(
        @NotBlank String name,
        @NotBlank String cpf,
        @NotBlank @Email String email,
        String phone
) {}