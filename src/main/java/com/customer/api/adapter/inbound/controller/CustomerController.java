package com.customer.api.adapter.inbound.controller;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.CreateCustomerCommand;
import com.customer.api.application.port.in.CreateCustomerUseCase;
import com.customer.api.application.port.in.GetCustomerQuery;
import com.customer.api.application.port.in.GetCustomerUseCase;
import com.customer.api.domain.CustomerId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;

    CustomerController(CreateCustomerUseCase createCustomerUseCase, GetCustomerUseCase getCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateCustomerCommand command) {
        CustomerId customerId = createCustomerUseCase.execute(command);
        return ResponseEntity
                .created(URI.create("/api/v1/customers/" + customerId.value()))
                .build();
    }

    @GetMapping("/{id}")
    ResponseEntity<CustomerDto> get(@PathVariable UUID id) {
        CustomerDto customer = getCustomerUseCase.execute(new GetCustomerQuery(CustomerId.from(id.toString())));
        return ResponseEntity.ok(customer);
    }
}