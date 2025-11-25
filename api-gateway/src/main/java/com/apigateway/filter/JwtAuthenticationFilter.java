package com.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "secret12345";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("GATEWAY PATH = " + path);

        // ------------- PUBLIC ENDPOINTS (NO TOKEN REQUIRED) -------------
        if (isPublicEndpoint(path)) {
            System.out.println("Public endpoint → allowed");
            return chain.filter(exchange);
        }

        // ------------- CHECK AUTHORIZATION HEADER -------------
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            // ------------- VERIFY JWT TOKEN -------------
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);

            String username = jwt.getSubject();
            String role = jwt.getClaim("role").asString();

            System.out.println("✔ VALID TOKEN → User=" + username + " Role=" + role);

            // ------------- ROLE-BASED AUTHORIZATION -------------
            if (!isAuthorized(path, role)) {
                System.out.println("❌ ACCESS DENIED → Role=" + role);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // ------------- ADD USER INFO TO HEADERS -------------
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Role", role)
                            .header("X-User-Name", username))
                    .build();

        } catch (JWTVerificationException e) {
            System.out.println("❌ INVALID TOKEN: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    // ===========================================================
    // PUBLIC ENDPOINTS → ALLOWED WITHOUT JWT
    // ===========================================================
    private boolean isPublicEndpoint(String path) {

        // Login
        if (path.startsWith("/auth/api/v1/auth/login"))
            return true;

        // Register
        if (path.startsWith("/auth/api/v1/auth/register"))
            return true;

        // 👉 VERY IMPORTANT: my-token endpoint must be public
        if (path.startsWith("/auth/api/v1/auth/my-token"))
            return true;

        // Public property search
        if (path.startsWith("/api/v1/property/search-property"))
            return true;

        // Payment status
        if (path.startsWith("/product/v1/success") ||
                path.startsWith("/product/v1/cancel"))
            return true;

        return false;
    }

    // ===========================================================
    // ROLE-BASED PROTECTION
    // ===========================================================
    private boolean isAuthorized(String path, String role) {

        // ADMIN ONLY
        if (path.startsWith("/auth/api/v1/auth/register-owner") ||
                path.startsWith("/auth/api/v1/auth/register-admin") ||
                path.startsWith("/auth/api/v1/location")) {

            return role.equals("ROLE_ADMIN");
        }

        // OWNER + ADMIN
        if (path.startsWith("/auth/api/v1/property/add-property")) {
            return role.equals("ROLE_OWNER") || role.equals("ROLE_ADMIN");
        }

        // All authenticated users allowed
        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
