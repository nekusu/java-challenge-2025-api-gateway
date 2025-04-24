package com.sharks.api_gateway.config;

import java.util.List;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    private final JwtUtils jwtUtils;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtUtils jwtUtils, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("users-service", r -> r.path("/users/**")
                        .uri("lb://users-service"))
                .route("users-service", r -> r.path("/api/auth/**")
                        .uri("lb://users-service"))
                .route("users-service", r -> r.path("/api/users/self")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://users-service"))
                .route("users-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.GET))))
                        .uri("lb://users-service"))
                .route("sale-points-service", r -> r.path("/sale-points/**")
                        .uri("lb://sale-points-service"))
                .route("sale-points-service", r -> r.path("/api/sale-points/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))))
                        .uri("lb://sale-points-service"))
                .route("sale-points-service", r -> r.path("/api/paths/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))))
                        .uri("lb://sale-points-service"))
                .route("accreditations-service", r -> r.path("/accreditations/**")
                        .uri("lb://accreditations-service"))
                .route("accreditations-service", r -> r.path("/api/accreditations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://accreditations-service"))
                .build();
    }

    private AdminAccessFilter adminAccessFilter(List<HttpMethod> requiredMethods) {
        return new AdminAccessFilter(jwtUtils, requiredMethods);
    }
}
