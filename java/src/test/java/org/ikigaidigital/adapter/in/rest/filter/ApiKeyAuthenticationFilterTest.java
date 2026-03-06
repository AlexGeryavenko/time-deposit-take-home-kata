package org.ikigaidigital.adapter.in.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

  private static final String VALID_KEY = "test-api-key-12345";

  private ApiKeyAuthenticationFilter filter;

  @Mock
  private FilterChain filterChain;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    filter = new ApiKeyAuthenticationFilter(VALID_KEY);
    request = new MockHttpServletRequest();
    request.setRequestURI("/api/v1/time-deposits");
    response = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateWithValidApiKey() throws ServletException, IOException {
    request.addHeader("X-API-Key", VALID_KEY);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldRejectMissingApiKey() throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void shouldRejectInvalidApiKey() throws ServletException, IOException {
    request.addHeader("X-API-Key", "wrong-key");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void shouldSkipNonApiPaths() throws ServletException, IOException {
    request.setRequestURI("/swagger-ui/index.html");

    filter.doFilter(request, response, filterChain);

    assertThat(response.getStatus()).isEqualTo(200);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldMaskShortKeyAsStars() throws ServletException, IOException {
    request.addHeader("X-API-Key", "ab");

    filter.doFilterInternal(request, response, filterChain);

    assertThat(response.getStatus()).isEqualTo(401);
    String body = response.getContentAsString();
    assertThat(body).doesNotContain("ab");
  }
}
