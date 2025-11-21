package com.customer.api.e2e.steps;

import com.customer.api.e2e.TestContext;
import com.customer.api.domain.Cpf;
import io.cucumber.java.pt.Dado;
import org.springframework.beans.factory.annotation.Autowired;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DataSetupSteps {

    @Autowired
    private TestContext context;

    private String createCustomer(String body) {
        Response response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v1/customers")
                .then()
                .extract()
                .response();
        String location = response.getHeader("Location");
        if (location != null && location.contains("/")) {
            return location.substring(location.lastIndexOf("/") + 1);
        }
        return null;
    }

    private String generateValidCpf(int seed) {
        int base = Math.abs(seed) % 1000000000;
        String nine = String.format("%09d", base);
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (nine.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (sum % 11); if (d1 > 9) d1 = 0;
        String ten = nine + d1;
        sum = 0;
        for (int i = 0; i < 10; i++) sum += (ten.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (sum % 11); if (d2 > 9) d2 = 0;
        return nine + d1 + d2;
    }

    private String ensureValidCpf(String cpfCandidate) {
        try { new Cpf(cpfCandidate); return cpfCandidate; } catch (Exception e) {
            return generateValidCpf((int)(System.nanoTime() & 0xFFFFFF));
        }
    }

    @Dado("existe um cliente com CPF {string}")
    public void existeUmClienteComCPF(String cpf) {
        String validCpf = ensureValidCpf(cpf);
        String body = String.format("""
            {
              "name": "Setup %s",
              "cpf": "%s",
              "email": "setup+%s@test.com"
            }
            """, validCpf, validCpf, validCpf);
        String id = createCustomer(body);
        context.set("lastCustomerId", id);
    }

    @Dado("existe cliente A com CPF {string}")
    public void existeClienteAComCpf(String cpf) {
        existeUmClienteComCPF(cpf);
        context.set("clienteACpf", cpf);
    }

    @Dado("existe um cliente com email {string}")
    public void existeUmClienteComEmail(String email) {
        String validCpf = generateValidCpf((int)(System.nanoTime() & 0xFFFFFF));
        String body = String.format("""
            {
              "name": "Setup %s",
              "cpf": "%s",
              "email": "%s"
            }
            """, email, validCpf, email);
        String id = createCustomer(body);
        context.set("lastCustomerId", id);
    }

    @Dado("existem {int} clientes cadastrados")
    public void existemNClientesCadastrados(Integer n) {
        for (int i = 0; i < n; i++) {
            String cpf = generateValidCpf(i + 1000);
            String body = String.format("""
                {
                  "name":"Joao %d",
                  "cpf":"%s",
                  "email":"joao%d@test.com"
                }
                """, i, cpf, i);
            createCustomer(body);
        }
    }

    @Dado("existe cliente B com ID conhecido")
    public void existeClienteBComIdConhecido() {
        String cpf = generateValidCpf(999999);
        String body = String.format("""
            {
              "name": "Cliente B",
              "cpf": "%s",
              "email": "clienteb@test.com"
            }
            """, cpf);
        String id = createCustomer(body);
        context.set("clientBId", id);
    }

    @Dado("existe um cliente com ID conhecido")
    public void existeUmClienteComIdConhecido() {
        String cpf = generateValidCpf(888888);
        String body = String.format("""
            {
              "name": "Cliente ToDelete",
              "cpf": "%s",
              "email": "todelete@test.com"
            }
            """, cpf);
        String id = createCustomer(body);
        context.set("knownId", id);
    }
}
