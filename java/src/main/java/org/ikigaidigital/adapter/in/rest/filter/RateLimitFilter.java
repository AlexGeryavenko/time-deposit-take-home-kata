package org.ikigaidigital.adapter.in.rest.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "AUDIT")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitFilter extends OncePerRequestFilter {

  private static final String RETRY_AFTER_HEADER = "Retry-After";

  private final int maxRequests;
  private final int windowSeconds;
  private final Cache<String, AtomicInteger> requestCounts;

  public RateLimitFilter(
      @Value("${api.rate-limit.max-requests:60}") int maxRequests,
      @Value("${api.rate-limit.window-seconds:60}") int windowSeconds) {
    this.maxRequests = maxRequests;
    this.windowSeconds = windowSeconds;
    this.requestCounts = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(windowSeconds))
        .build();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String clientIp = request.getRemoteAddr();
    AtomicInteger count = requestCounts.get(clientIp, k -> new AtomicInteger(0));

    int currentCount = count.incrementAndGet();
    if (currentCount > maxRequests) {
      MDC.put("rateLimitOutcome", "throttled");
      MDC.put("rateLimitCount", String.valueOf(currentCount));
      log.warn("Rate limit exceeded");

      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setHeader(RETRY_AFTER_HEADER, String.valueOf(windowSeconds));
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(
          "{\"error\":\"TOO_MANY_REQUESTS\","
              + "\"message\":\"Rate limit exceeded. Please retry after " + windowSeconds + " seconds.\","
              + "\"status\":" + HttpStatus.TOO_MANY_REQUESTS.value() + "}");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
