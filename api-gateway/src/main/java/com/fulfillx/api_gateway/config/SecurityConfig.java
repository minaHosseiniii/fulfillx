package com.fulfillx.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Customer
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers")
                        .hasAnyRole("SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/**")
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/**")
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/customers/**")
                        .hasAnyRole("SUPPORT", "ADMIN")

                        // Order
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // Payment

                        .requestMatchers(HttpMethod.POST, "/api/v1/payments")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/payments/by-idempotency-key"
                        )
                        .hasAnyRole("SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/*")
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/*/cancel")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // Catalog

                        .requestMatchers(HttpMethod.GET, "/api/v1/products/*")
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/products")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/*")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/products/*/activate",
                                "/api/v1/products/*/deactivate"
                        )
                        .hasRole("ADMIN")

                        // Delivery

                        .requestMatchers(HttpMethod.POST, "/api/v1/deliveries")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/deliveries/order/*",
                                "/api/v1/deliveries/*"
                        )
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/deliveries/**")
                        .hasRole("ADMIN")

                        // Inventory

                        .requestMatchers(HttpMethod.GET, "/api/v1/inventory/*")
                        .hasAnyRole("CUSTOMER", "SUPPORT", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/inventory")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/inventory/*/reserve",
                                "/api/v1/inventory/*/release",
                                "/api/v1/inventory/*/deduct"
                        )

                        .denyAll()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {

            if (jwt.getAudience().contains("fulfillx-api")) {
                return OAuth2TokenValidatorResult.success();
            }

            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "The required audience is missing",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        };

        OAuth2TokenValidator<Jwt> defaultValidator =
                JwtValidators.createDefaultWithIssuer(issuerUri);

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        defaultValidator,
                        audienceValidator
                )
        );

        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter defaultConverter =
                new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities =
                    new ArrayList<>(defaultConverter.convert(jwt));

            Map<String, Object> realmAccess =
                    jwt.getClaim("realm_access");

            if (realmAccess != null) {

                List<String> roles =
                        (List<String>) realmAccess.get("roles");

                if (roles != null) {
                    roles.forEach(role ->
                            authorities.add(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                            )
                    );
                }
            }

            return authorities;
        });

        return converter;
    }
}
