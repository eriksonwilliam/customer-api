package com.customer.api.domain;

import com.customer.api.domain.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfTest {

    @Test
    void cpfValido() {
        assertDoesNotThrow(() -> new Cpf("52998224725"));
    }

    @Test
    void cpfInvalido() {
        assertThrows(InvalidCpfException.class, () -> new Cpf("11111111111"));
        assertThrows(InvalidCpfException.class, () -> new Cpf("52998224700"));
        assertThrows(InvalidCpfException.class, () -> new Cpf("123"));
        assertThrows(InvalidCpfException.class, () -> new Cpf(null));
    }
}