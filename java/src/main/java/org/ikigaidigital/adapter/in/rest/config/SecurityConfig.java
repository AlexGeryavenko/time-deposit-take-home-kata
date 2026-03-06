package org.ikigaidigital.adapter.in.rest.config;

import java.time.Instant;
import java.util.List;
import org.ikigaidigital.adapter.in.rest.filter.ApiKeyAuthenticationFilter;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final long HSTS_MAX_AGE_SECONDS = 31_536_000L;
  private static final long CORS_MAX_AGE_SECONDS = 3600L;
  private static final String CSP_DIRECTIVES = "default-src 'self'; frame-ancestors 'none'";
  private static final String PERMISSIONS_POLICY = "geolocation=(), camera=(), microphone=()";

  @Value("${api.security.api-key:changeme-dev-key}")
  private String apiKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(new ApiKeyAuthenticationFilter(apiKey),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType(MediaType.APPLICATION_JSON_VALUE);
              String correlationId = MDC.get("correlationId");
              String cidField = correlationId != null
                  ? ",\"correlationId\":\"" + correlationId + "\""
                  : "";
              response.getWriter().write(
                  "{\"error\":\"UNAUTHORIZED\","
                      + "\"message\":\"Missing or invalid API key. Provide a valid X-API-Key header.\","
                      + "\"status\":" + HttpStatus.UNAUTHORIZED.value() + ","
                      + "\"timestamp\":\"" + Instant.now() + "\""
                      + cidField + "}");
            }))
        .headers(headers -> headers
            .contentTypeOptions(contentType -> {
            })
            .frameOptions(FrameOptionsConfig::deny)
            .httpStrictTransportSecurity(hsts -> hsts
                .maxAgeInSeconds(HSTS_MAX_AGE_SECONDS)
                .includeSubDomains(true))
            .contentSecurityPolicy(csp -> csp.policyDirectives(CSP_DIRECTIVES))
            .referrerPolicy(referrer -> referrer
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(permissions -> permissions.policy(PERMISSIONS_POLICY)))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*"));
    config.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name()));
    config.setAllowedHeaders(List.of("Content-Type", "X-API-Key"));
    config.setMaxAge(CORS_MAX_AGE_SECONDS);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
