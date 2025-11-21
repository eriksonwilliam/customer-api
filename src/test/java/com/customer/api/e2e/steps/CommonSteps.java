package com.customer.api.e2e.steps;

import com.customer.api.e2e.TestContext;
import com.customer.api.adapter.outbound.persistence.CustomerJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class CommonSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext context;

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        context.clear();
        customerJpaRepository.deleteAll();
    }

    @Dado("que a API esta rodando")
    public void queApiEstaRodando() { }
}
