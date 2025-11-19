package com.customer.api.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerCommand(
        String name,
        String cpf,
        @Email String email,
        String phone
) {}