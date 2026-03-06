package org.ikigaidigital.adapter.in.rest.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.adapter.in.rest.generated.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    MDC.put("correlationId", "test-corr-id");
  }

  @Test
  void shouldHandleBadRequest() {
    ResponseEntity<ErrorResponse> response = handler.handleBadRequest(
        new IllegalArgumentException("invalid input"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).isEqualTo("invalid input");
    assertThat(response.getBody().getCorrelationId()).isEqualTo("test-corr-id");
  }

  @Test
  void shouldHandleMethodNotAllowed() {
    ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(
        new HttpRequestMethodNotSupportedException("DELETE"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    assertThat(response.getBody().getStatus()).isEqualTo(405);
  }

  @Test
  void shouldHandleForbidden() {
    ResponseEntity<ErrorResponse> response = handler.handleForbidden(
        new AccessDeniedException("forbidden"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody().getStatus()).isEqualTo(403);
    assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
  }

  @Test
  void shouldHandleInternalError() {
    ResponseEntity<ErrorResponse> response = handler.handleInternalError(
        new RuntimeException("unexpected"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().getStatus()).isEqualTo(500);
    assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
  }

  @Test
  void shouldIncludeCorrelationIdInResponse() {
    ResponseEntity<ErrorResponse> response = handler.handleInternalError(
        new RuntimeException("test"));

    assertThat(response.getBody().getCorrelationId()).isEqualTo("test-corr-id");
  }
}
