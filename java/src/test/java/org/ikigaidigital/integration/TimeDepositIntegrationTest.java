package org.ikigaidigital.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = {"/cleanup.sql", "/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Time Deposit REST API integration tests")
class TimeDepositIntegrationTest {

    private static final String API_KEY = "changeme-dev-key";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.cache.type", () -> "none");
    }

    private static HttpEntity<Void> authenticatedRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", API_KEY);
        return new HttpEntity<>(headers);
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/v1/time-deposits returns all deposits with withdrawals")
    void shouldReturnDepositsWithWithdrawals() {
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "/api/v1/time-deposits?page=0&size=100",
            HttpMethod.GET, authenticatedRequest(), JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        JsonNode content = response.getBody().get("content");
        assertThat(content).hasSize(3);

        JsonNode basic = content.get(0);
        assertThat(basic.get("planType").asText()).isEqualTo("basic");
        assertThat(basic.get("balance").asDouble()).isEqualTo(1000.0);
        assertThat(basic.get("days").asInt()).isEqualTo(31);
        assertThat(basic.get("withdrawals")).hasSize(1);
        assertThat(basic.get("withdrawals").get(0).get("amount").asDouble()).isEqualTo(100.0);

        assertThat(content.get(1).get("planType").asText()).isEqualTo("student");
        assertThat(content.get(2).get("planType").asText()).isEqualTo("premium");
        assertThat(response.getBody().get("totalElements").asInt()).isEqualTo(3);
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/v1/time-deposits/update-balances calculates interest correctly")
    void shouldUpdateBalancesWithCorrectInterest() {
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "/api/v1/time-deposits/update-balances",
            HttpMethod.POST, authenticatedRequest(), JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);

        // basic 31d 1000 → +0.83, student 31d 2000 → +5.00, premium 46d 10000 → +41.67
        JsonNode deposits = response.getBody();
        assertThat(deposits.get(0).get("balance").asDouble()).isEqualTo(1000.83);
        assertThat(deposits.get(1).get("balance").asDouble()).isEqualTo(2005.0);
        assertThat(deposits.get(2).get("balance").asDouble()).isEqualTo(10041.67);
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/v1/time-deposits/update-balances persists updated balances")
    void shouldPersistUpdatedBalances() {
        restTemplate.exchange("/api/v1/time-deposits/update-balances",
            HttpMethod.POST, authenticatedRequest(),
            new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "/api/v1/time-deposits?page=0&size=100",
            HttpMethod.GET, authenticatedRequest(), JsonNode.class);

        assertThat(response.getBody()).isNotNull();
        JsonNode content = response.getBody().get("content");
        assertThat(content.get(0).get("balance").asDouble()).isEqualTo(1000.83);
        assertThat(content.get(1).get("balance").asDouble()).isEqualTo(2005.0);
        assertThat(content.get(2).get("balance").asDouble()).isEqualTo(10041.67);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/v1/time-deposits without API key returns 401")
    void shouldReturn401WithoutApiKey() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "/api/v1/time-deposits",
            HttpMethod.GET, null,
            new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Missing API key");
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/v1/time-deposits with invalid API key returns 401")
    void shouldReturn401WithInvalidApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", "wrong-key");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "/api/v1/time-deposits",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Invalid API key");
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/v1/time-deposits supports pagination")
    void shouldSupportPagination() {
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            "/api/v1/time-deposits?page=0&size=2",
            HttpMethod.GET, authenticatedRequest(), JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("content")).hasSize(2);
        assertThat(response.getBody().get("totalElements").asInt()).isEqualTo(3);
        assertThat(response.getBody().get("totalPages").asInt()).isEqualTo(2);
        assertThat(response.getBody().get("page").asInt()).isEqualTo(0);
        assertThat(response.getBody().get("size").asInt()).isEqualTo(2);
    }
}
