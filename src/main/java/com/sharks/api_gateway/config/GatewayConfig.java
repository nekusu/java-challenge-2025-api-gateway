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

    private static final String USERS_SERVICE_ROUTE = "users-service";
    private static final String SALE_POINTS_SERVICE_ROUTE = "sale-points-service";
    private static final String ACCREDITATIONS_SERVICE_ROUTE = "accreditations-service";
    private static final String USERS_SERVICE_URI = "lb://users-service";
    private static final String SALE_POINTS_SERVICE_URI = "lb://sale-points-service";
    private static final String ACCREDITATIONS_SERVICE_URI = "lb://accreditations-service";

    public GatewayConfig(JwtUtils jwtUtils, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(USERS_SERVICE_ROUTE, r -> r.path("/users/**")
                        .uri(USERS_SERVICE_URI))
                .route(USERS_SERVICE_ROUTE, r -> r.path("/api/auth/**")
                        .uri(USERS_SERVICE_URI))
                .route(USERS_SERVICE_ROUTE, r -> r.path("/api/users/self")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri(USERS_SERVICE_URI))
                .route(USERS_SERVICE_ROUTE, r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.GET))))
                        .uri(USERS_SERVICE_URI))
                .route(SALE_POINTS_SERVICE_ROUTE, r -> r.path("/sale-points/**")
                        .uri(SALE_POINTS_SERVICE_URI))
                .route(SALE_POINTS_SERVICE_ROUTE, r -> r.path("/api/sale-points/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))))
                        .uri(SALE_POINTS_SERVICE_URI))
                .route(SALE_POINTS_SERVICE_ROUTE, r -> r.path("/api/paths/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)
                                .filter(adminAccessFilter(List.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))))
                        .uri(SALE_POINTS_SERVICE_URI))
                .route(ACCREDITATIONS_SERVICE_ROUTE, r -> r.path("/accreditations/**")
                        .uri(ACCREDITATIONS_SERVICE_URI))
                .route(ACCREDITATIONS_SERVICE_ROUTE, r -> r.path("/api/accreditations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri(ACCREDITATIONS_SERVICE_URI))
                .build();
    }

    private AdminAccessFilter adminAccessFilter(List<HttpMethod> requiredMethods) {
        return new AdminAccessFilter(jwtUtils, requiredMethods);
    }
}
