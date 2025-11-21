package com.customer.api.e2e.steps;

import com.customer.api.e2e.TestContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.restassured.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerSteps {

    @Autowired
    private TestContext context;

    @Dado("que eu possuo um cliente com id {string}")
    public void queEuPossuoUmClienteComId(String id) { context.set("customerId", id); }

    @Quando("eu realizo a consulta do cliente")
    public void euRealizoConsultaCliente() {
        String id = context.get("customerId", String.class);
        Response response = RestAssured.given().baseUri("http://localhost:8080").get("/customers/" + id);
        context.set("response", response);
    }

    @Quando("eu crio um cliente com nome {string} e email {string}")
    public void euCrioUmCliente(String name, String email) {
        Response response = RestAssured.given().baseUri("http://localhost:8080")
                .contentType("application/json")
                .body("""
                       {
                         "name": "%s",
                         "email": "%s"
                       }
                       """.formatted(name, email))
                .post("/customers");
        context.set("response", response);
    }

    @Quando("eu deleto o cliente informado")
    public void eudeletoCliente() {
        String id = context.get("customerId", String.class);
        Response response = RestAssured.given().baseUri("http://localhost:8080").delete("/customers/" + id);
        context.set("response", response);
    }

    @Quando("eu envio POST para {string} com:")
    public void euEnvioPOSTParaCom(String path, String body) {
        Response response = RestAssured.given().contentType(ContentType.JSON).body(body).post(path);
        context.set("response", response);
        String location = response.getHeader("Location");
        if (location != null && location.contains("/")) {
            context.set("lastCreatedId", location.substring(location.lastIndexOf("/") + 1));
        }
    }

    private String resolveId() {
        Object id = context.get("lastCreatedId");
        if (id == null) id = context.get("knownId");
        if (id == null) id = context.get("clientBId");
        if (id == null) id = context.get("customerId");
        return id != null ? id.toString() : null;
    }

    @Quando("eu envio GET para {string}")
    public void euEnvioGETPara(String path) {
        if (path.contains("{id}")) {
            String id = resolveId();
            if (id != null) path = path.replace("{id}", id);
        }
        Response response = RestAssured.get(path);
        context.set("response", response);
    }

    @Quando("eu envio PUT para {string} com:")
    public void euEnvioPUTParaCom(String path, String body) {
        if (path.contains("{id}")) {
            String id = resolveId();
            if (id != null) path = path.replace("{id}", id);
        }
        Response response = RestAssured.given().contentType(ContentType.JSON).body(body).put(path);
        context.set("response", response);
    }

    @Quando("eu envio DELETE para {string}")
    public void euEnvioDELETEPara(String path) {
        if (path.contains("{id}")) {
            String id = resolveId();
            if (id != null) path = path.replace("{id}", id);
        }
        Response response = RestAssured.delete(path);
        context.set("response", response);
    }
}
