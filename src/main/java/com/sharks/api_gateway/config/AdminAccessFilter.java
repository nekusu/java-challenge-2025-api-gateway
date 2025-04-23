package com.sharks.api_gateway.config;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AdminAccessFilter implements GatewayFilter {

    private final JwtUtils jwtUtils;

    private final List<HttpMethod> requiredMethods;

    public AdminAccessFilter(JwtUtils jwtUtils, List<HttpMethod> requiredMethods) {
        this.jwtUtils = jwtUtils;
        this.requiredMethods = requiredMethods;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = jwtUtils.extractTokenFromExchange(exchange);
        Claims claims = jwtUtils.parseClaims(token);
        String role = claims.get("role", String.class);

        if (requiredMethods.contains(request.getMethod()) && (role == null || !role.equals("ADMIN")))
            return onError(exchange, String.format("%s %s: Access denied for user with role %s",
                    request.getMethod(), request.getPath(), role));

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        log.error(error);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
