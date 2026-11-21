package com.fulfillx.order.config;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor bearerTokenRequestInterceptor() {

        return requestTemplate -> {

            var authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            System.out.println("Authentication: " + authentication);
            System.out.println("Authentication Type: " +
                    (authentication != null
                            ? authentication.getClass().getName()
                            : "null"));

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {

                String token = jwtAuth.getToken().getTokenValue();
                System.out.println("JWT token found: " + (jwtAuth.getToken() != null));

                requestTemplate.header(
                        "Authorization",
                        "Bearer " + token
                );
            }
        };
    }
}

