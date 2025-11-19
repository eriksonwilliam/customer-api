package com.customer.api.adapter.inbound.controller;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.*;
import com.customer.api.domain.CustomerId;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;

    CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase,
            UpdateCustomerUseCase updateCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
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

    @GetMapping
    ResponseEntity<Page<CustomerDto>> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {

        var query = new ListCustomersQuery(search, pageable);
        Page<CustomerDto> page = listCustomersUseCase.execute(query);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateCustomerCommand command) {
        updateCustomerUseCase.execute(CustomerId.from(id.toString()), command);
        return ResponseEntity.noContent().build();
    }
}