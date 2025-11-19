package com.customer.api.application.port.in;

import org.springframework.data.domain.Pageable;

public record ListCustomersQuery(String search, Pageable pageable) {}