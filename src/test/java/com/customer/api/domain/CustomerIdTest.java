package com.customer.api.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;

class CustomerIdTest {
    @Test
    void generateAndFromString() {
        CustomerId id = CustomerId.generate();
        assertNotNull(id.value());
        String uuid = id.value().toString();
        CustomerId from = CustomerId.from(uuid);
        assertEquals(id.value(), from.value());
    }

    @Test
    void fromString_invalidUUID_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> CustomerId.from("invalid-uuid"));
    }

    @Test
    void equalsAndHashCode() {
        UUID uuid = UUID.randomUUID();
        CustomerId id1 = new CustomerId(uuid);
        CustomerId id2 = new CustomerId(uuid);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void valueIsNotNull() {
        assertThrows(NullPointerException.class, () -> new CustomerId(null));
    }
}
