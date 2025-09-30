package resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.RequestDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import service.ProdutosService;

@QuarkusTest
@TestHTTPEndpoint(ProdutosResource.class)
class ProdutosResourceAuthIT {

    @Inject
    ProdutosService produtosService;

    private Long produtoId;

    @BeforeEach
    void setup() {
        // Limpar base antes de cada teste
        produtosService.findAllProdutos().forEach(p -> produtosService.deleteProduto(p.id()));

        // Criar produto inicial
        RequestDTO dto = new RequestDTO("Café", "Café preto forte", new BigDecimal("9.90"));
        produtosService.criaProduto(dto);
        produtoId = produtosService.findAllProdutos().get(0).id();
    }

    // ------------------- TESTES COM ROLE ADMIN -------------------

    @Test
    @TestSecurity(user = "adminUser", roles = { "admin" })
    void testCreateProdutoAsAdmin() {
        RequestDTO dto = new RequestDTO("Chá", "Chá verde", new BigDecimal("7.50"));

        given()
            .contentType("application/json")
            .body(dto)
        .when()
            .post()
        .then()
            .statusCode(201);

        given()
            .when()
            .get()
        .then()
            .statusCode(200)
            .body("nome", hasItems("Café", "Chá"));
    }

    @Test
    @TestSecurity(user = "adminUser", roles = { "admin" })
    void testUpdateProdutoAsAdmin() {
        RequestDTO dto = new RequestDTO("Café Atualizado", "Descrição nova", new BigDecimal("10.50"));

        given()
            .contentType("application/json")
            .pathParam("id", produtoId)
            .body(dto)
        .when()
            .put("/{id}")
        .then()
            .statusCode(200);

        given()
            .pathParam("id", produtoId)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .body("nome", is("Café Atualizado"))
            .body("preco", is(10.50f));
    }

    @Test
    @TestSecurity(user = "adminUser", roles = { "admin" })
    void testDeleteProdutoAsAdmin() {
        given()
            .pathParam("id", produtoId)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(204);

        given()
            .pathParam("id", produtoId)
        .when()
            .get("/{id}")
        .then()
            .statusCode(404);
    }

    // ------------------- TESTES COM ROLE USER -------------------

    @Test
    @TestSecurity(user = "normalUser", roles = { "user" })
    void testConsultaProdutosAsUser() {
        given()
            .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].nome", is("Café"));
    }

    @Test
    @TestSecurity(user = "normalUser", roles = { "user" })
    void testConsultaProdutoPorIdExistsAsUser() {
        given()
            .pathParam("id", produtoId)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .body("nome", is("Café"))
            .body("descricao", is("Café preto forte"));
    }

    @Test
    @TestSecurity(user = "normalUser", roles = { "user" })
    void testConsultaProdutoPorIdNotExistsAsUser() {
        given()
            .pathParam("id", 999L)
        .when()
            .get("/{id}")
        .then()
            .statusCode(404);
    }

    // ------------------- TESTES SEM AUTENTICAÇÃO -------------------

    @Test
    void testAccessWithoutAuthentication() {
        given()
            .when()
            .get()
        .then()
            .statusCode(401); // Não autenticado
    }

}
