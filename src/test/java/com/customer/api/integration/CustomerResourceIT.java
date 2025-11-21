package com.customer.api.integration;

import com.customer.api.CustomerApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(classes = CustomerApiApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = CustomerResourceIT.Initializer.class)
class CustomerResourceIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword()
            ).applyTo(context.getEnvironment());
        }
    }

    @Autowired MockMvc mockMvc;

    @Test
    void crudCompleto() throws Exception {
        String json = """
            {"name":"Maria Silva","cpf":"52998224725","email":"maria@test.com","phone":"11987654321"}
            """;

        String location = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        String id = location.substring(location.lastIndexOf("/") + 1);

        mockMvc.perform(get("/api/v1/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Silva"));

        String update = """
            {"name":"Maria Oliveira","cpf":"52998224725","email":"maria.nova@test.com","phone":"11900001111"}
            """;

        mockMvc.perform(put("/api/v1/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/customers/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/customers/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoPermiteCpfDuplicado() throws Exception {
        String json1 = """
            {"name":"João","cpf":"98765432100","email":"joao@test.com","phone":"11988887777"}
            """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isCreated());

        String json2 = """
            {"name":"Maria","cpf":"98765432100","email":"maria@test.com"}
            """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cpfInvalidoRetorna400() throws Exception {
        String json = """
            {"name":"Teste","cpf":"11111111111","email":"teste@test.com"}
            """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void paginacaoRetornaMetadadosCorretos() throws Exception {
        String[] clientes = {
                "{\"name\":\"Cliente1\",\"cpf\":\"52998224725\",\"email\":\"c1@test.com\",\"phone\":\"11911111111\"}",
                "{\"name\":\"Cliente2\",\"cpf\":\"14538220620\",\"email\":\"c2@test.com\"}",
                "{\"name\":\"Cliente3\",\"cpf\":\"15350946056\",\"email\":\"c3@test.com\"}",
                "{\"name\":\"Cliente4\",\"cpf\":\"98765432100\",\"email\":\"c4@test.com\"}",
                "{\"name\":\"Cliente5\",\"cpf\":\"11144477735\",\"email\":\"c5@test.com\"}"
        };
        for (String json : clientes) {
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/customers?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/api/v1/customers?page=2&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
