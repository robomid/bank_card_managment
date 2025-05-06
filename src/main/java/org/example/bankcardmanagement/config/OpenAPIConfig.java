package org.example.bankcardmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI productServiceAPI() {
        return new OpenAPI()
        .info(new Info().title("Bank Card Management")
                .description("This is the demo version of the Bank Card Management API.")
                .version("v0.0.1"));
    }
}
