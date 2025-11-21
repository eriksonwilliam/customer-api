package com.customer.api.adapter.inbound.controller;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.dto.PageResponse;
import com.customer.api.application.port.in.*;
import com.customer.api.domain.CustomerId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Gestão de clientes")
class CustomerResource {

    private static final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    CustomerResource(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase) {

        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    @Operation(summary = "Criar novo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já existente")
    })
    @PostMapping
    ResponseEntity<Void> create(@org.springframework.web.bind.annotation.RequestBody @Valid CreateCustomerCommand command) {
        CustomerId customerId = createCustomerUseCase.execute(command);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + customerId.value())).build();
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<CustomerDto> findById(@PathVariable(name="id") UUID id) {
        log.debug("GET /api/v1/customers/{}", id);
        CustomerDto customer = getCustomerUseCase.execute(new GetCustomerQuery(CustomerId.from(id.toString())));
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Listar clientes com paginação e filtro")
    @GetMapping(produces = "application/json")
    ResponseEntity<PageResponse<CustomerDto>> list(
            @RequestParam(name="search", required = false) String search,
            @ParameterObject Pageable pageable) {
        if (pageable == null) pageable = Pageable.ofSize(20);
        log.debug("LIST /api/v1/customers?search={}", search);
        Page<CustomerDto> page = listCustomersUseCase.execute(new ListCustomersQuery(search, pageable));
        PageResponse<CustomerDto> dto = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Atualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable(name="id") UUID id,
                                @org.springframework.web.bind.annotation.RequestBody @Valid UpdateCustomerCommand command) {
        log.debug("PUT /api/v1/customers/{}", id);
        updateCustomerUseCase.execute(CustomerId.from(id.toString()), command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir cliente (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable(name="id") UUID id) {
        log.debug("DELETE /api/v1/customers/{}", id);
        deleteCustomerUseCase.execute(CustomerId.from(id.toString()));
        return ResponseEntity.noContent().build();
    }
}