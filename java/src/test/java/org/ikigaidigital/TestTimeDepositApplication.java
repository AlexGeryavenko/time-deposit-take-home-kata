package org.ikigaidigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestTimeDepositApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "seed");
        SpringApplication.from(TimeDepositApplication::main)
            .with(ContainerConfig.class)
            .run(args);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ContainerConfig {

        @Bean
        @ServiceConnection
        PostgreSQLContainer<?> postgresContainer() {
            return new PostgreSQLContainer<>("postgres:16-alpine");
        }
    }
}
