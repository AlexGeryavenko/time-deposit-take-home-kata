package org.ikigaidigital.adapter.in.rest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "AUDIT")
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-Key";

  private final String expectedApiKey;

  public ApiKeyAuthenticationFilter(String expectedApiKey) {
    this.expectedApiKey = expectedApiKey;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return !path.startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String apiKey = request.getHeader(API_KEY_HEADER);

    if (apiKey == null || !MessageDigest.isEqual(
        expectedApiKey.getBytes(), apiKey.getBytes())) {
      MDC.put("apiKeyId", apiKey == null ? "absent" : maskApiKey(apiKey));
      MDC.put("authOutcome", "failure");
      log.warn("Authentication failed");
      writeUnauthorizedResponse(response);
      return;
    }

    MDC.put("apiKeyId", maskApiKey(apiKey));
    MDC.put("authOutcome", "success");
    log.debug("Authentication successful");

    var authentication = new PreAuthenticatedAuthenticationToken(
        "api-key-user", null, AuthorityUtils.NO_AUTHORITIES);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }

  private String maskApiKey(String key) {
    if (key.length() <= 4) {
      return "****";
    }
    return "****" + key.substring(key.length() - 4);
  }

  private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(
        "{\"error\":\"UNAUTHORIZED\","
            + "\"message\":\"Missing or invalid API key. Provide a valid X-API-Key header.\","
            + "\"status\":" + HttpStatus.UNAUTHORIZED.value() + ","
            + "\"timestamp\":\"" + Instant.now() + "\"}");
  }
}
