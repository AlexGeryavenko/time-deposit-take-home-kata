package org.ikigaidigital.adapter.in.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuditLoggingFilterTest {

  private AuditLoggingFilter filter;

  @Mock
  private FilterChain filterChain;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    filter = new AuditLoggingFilter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    MDC.clear();
  }

  @Test
  void shouldUseProvidedCorrelationId() throws ServletException, IOException {
    request.addHeader("X-Correlation-Id", "test-123");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("test-123");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldGenerateCorrelationIdWhenMissing() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getHeader("X-Correlation-Id")).isNotNull();
    assertThat(response.getHeader("X-Correlation-Id")).isNotBlank();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldClearMdcAfterRequest() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);

    assertThat(MDC.get("correlationId")).isNull();
  }

  @Test
  void shouldSetCorrelationIdInResponseHeader() throws ServletException, IOException {
    request.addHeader("X-Correlation-Id", "abc-456");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getHeader("X-Correlation-Id")).isEqualTo("abc-456");
  }
}
