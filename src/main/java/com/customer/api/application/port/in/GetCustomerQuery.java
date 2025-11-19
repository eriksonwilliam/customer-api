package com.customer.api.application.port.in;

import com.customer.api.domain.CustomerId;

public record GetCustomerQuery(CustomerId customerId) {}