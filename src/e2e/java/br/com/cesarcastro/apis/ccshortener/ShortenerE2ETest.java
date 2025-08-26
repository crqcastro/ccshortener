package br.com.cesarcastro.apis.ccshortener;

import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;

import static br.com.cesarcastro.apis.ccshortener.TestUtils.objectMapper;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ShortenerE2EH2Test {

    @Autowired
    private Flyway flyway;

    private ObjectMapper om;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false));

        om = objectMapper();
    }

    @AfterAll
    public void tearDown() {
        flyway.clean();
    }

    @Test
    @Order(1)
    @DisplayName("should shorten the URL successfully and retrieve the original URL")
    public void shouldShortenTheURLSuccessfully() throws JsonProcessingException {
        String originalUrl = "https://www.cesarcastro.com.br";
        var req = ShortenedURLRequestDTO.builder()
                .originalUrl(originalUrl)
                .build();

        var createResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(om.writeValueAsString(req))
                .when()
                .post("/v1/shortener")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("originalUrl", equalTo(originalUrl))
                .body("shortUrl", allOf(notNullValue(), matchesPattern("https?://.+/.+")))
                .body("createdAt", notNullValue())
                .body("expiresAt", notNullValue())
                .body("qrCodeImage", notNullValue())
                .extract();

        log.info("POST /v1/shortener -> status={} body=\n{}",
                createResp.statusCode(),
                createResp.body().asPrettyString());

        String shortUrl = createResp.jsonPath().getString("shortUrl");
        String code = URI.create(shortUrl).getPath().replaceFirst(".*/", "");

        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/v1/shortener/{code}", code)
                .then()
                .statusCode(anyOf(is(301), is(302)))
                .header("Location", equalTo(originalUrl));
    }

    @Test
    @Order(2)
    @DisplayName("should fail with 400 when the URL does not start with HTTP or HTTPS")
    void shouldFailWith400WhenURLnotStartWithHttpOrHttps() throws JsonProcessingException {
        String originalUrl = "www.cesarcastro.com.br";
        var req = ShortenedURLRequestDTO.builder()
                .originalUrl(originalUrl)
                .build();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(om.writeValueAsString(req))
                .when()
                .post("/v1/shortener")
                .then()
                .statusCode(400)
                .body(containsString("URL must start with http:// or https://"));
    }

    @Test
    @Order(3)
    @DisplayName("should fail with 400 when the URL starts with FTP")
    void shouldFailWith400WhenURLStartsWithFTP() throws JsonProcessingException {

        String originalUrl = "ftp://www.cesarcastro.com.br";
        var req = ShortenedURLRequestDTO.builder()
                .originalUrl(originalUrl)
                .build();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(om.writeValueAsString(req))
                .when()
                .post("/v1/shortener")
                .then()
                .statusCode(400)
                .body(containsString("URL must start with http:// or https://"));
    }
}
