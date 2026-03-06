package org.ikigaidigital.adapter.in.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Time Deposit API")
            .description("XA Bank time deposit interest calculation system")
            .version("0.6.0"))
        .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
        .components(new Components()
            .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")));
  }
}
