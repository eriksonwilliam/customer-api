package com.customer.api.e2e.steps;

import com.customer.api.e2e.TestContext;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.Normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationSteps {

    @Autowired
    private TestContext context;

    @Então("o código de resposta deve ser {int}")
    public void codigoResposta(int status) {
        Response response = context.get("response", Response.class);
        assertEquals(status, response.getStatusCode());
    }

    @Então("o campo {string} deve ser {string}")
    public void validarCampo(String campo, String valor) {
        Response response = context.get("response", Response.class);

        String bodyValue = response.jsonPath().getString(campo);
        assertEquals(valor, bodyValue);
    }

    @Então("o campo {string} deve existir")
    public void campoDeveExistir(String campo) {
        Response response = context.get("response", Response.class);

        Object value = response.jsonPath().get(campo);
        assertEquals(true, value != null);
    }

    @Então("o status deve ser {int}")
    public void oStatusDeveSer(int status) {
        Response response = context.get("response", Response.class);
        assertEquals(status, response.getStatusCode());
    }

    @Então("o header {string} deve conter {string}")
    public void oHeaderDeveConter(String headerName, String fragment) {
        Response response = context.get("response", Response.class);
        String value = response.getHeader(headerName);
        assertEquals(true, value != null && value.contains(fragment));
    }

    @Então("a resposta contem {string}")
    public void aRespostaContem(String texto) {
        Response response = context.get("response", Response.class);
        String body = response.getBody().asString();
        String normalizedBody = normalize(body);
        String normalizedExpected = normalize(texto);
        assertEquals(true, normalizedBody.contains(normalizedExpected), () -> "Esperado encontrar '"+texto+"' em: " + body);
    }

    private String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase();
    }

    @Então("a lista contem pelo menos {int} cliente")
    public void aListaContemPeloMenosCliente(int minimo) {
        Response response = context.get("response", Response.class);
        int size = response.jsonPath().getList("content").size();
        assertEquals(true, size >= minimo);
    }
}
