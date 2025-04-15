package com.sharks.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("sale-points-service", r -> r.path("/api/sale-points/**").uri("lb://sale-points-service"))
                .route("sale-points-service", r -> r.path("/api/paths/**").uri("lb://sale-points-service"))
                .route("accreditations-service",
                        r -> r.path("/api/accreditations/**").uri("lb://accreditations-service"))
                .build();
    }
}
