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

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {

                String token = jwtAuth.getToken().getTokenValue();

                requestTemplate.header(
                        "Authorization",
                        "Bearer " + token
                );
            }
        };
    }
}

