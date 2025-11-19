package com.customer.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Customer API",
                version = "1.0",
                description = "Microsserviço de domínio de clientes - Clean Architecture + DDD"
        )
)
class OpenApiConfig {}