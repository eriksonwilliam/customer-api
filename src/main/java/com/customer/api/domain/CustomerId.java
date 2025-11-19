package com.customer.api.domain;

import java.util.UUID;

public record CustomerId(UUID value) {
    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(String value) {
        return new CustomerId(UUID.fromString(value));
    }
}