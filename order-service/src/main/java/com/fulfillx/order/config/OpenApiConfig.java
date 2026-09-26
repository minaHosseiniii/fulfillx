package com.fulfillx.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fulfillXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FulfillX Order Service API")
                        .version("1.0.0")
                        .description(
                                "REST API for order management in the FulfillX platform."
                        )
                );
    }
}