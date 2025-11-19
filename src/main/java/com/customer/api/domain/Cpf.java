package com.customer.api.domain;

import com.customer.api.domain.exception.InvalidCpfException;

public record Cpf(String value) {

    public Cpf {
        if (value == null || !isValid(value)) {
            throw new InvalidCpfException(value);
        }
    }

    private static boolean isValid(String cpf) {
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11 || digits.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        if ((digits.charAt(9) - '0') != firstDigit) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (digits.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;

        return (digits.charAt(10) - '0') == secondDigit;
    }
}