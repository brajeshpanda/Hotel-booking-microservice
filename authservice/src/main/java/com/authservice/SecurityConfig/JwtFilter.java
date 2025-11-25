package com.authservice.SecurityConfig;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authservice.Service.CustomerUserDetailsService;
import com.authservice.Service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println(">>> JwtFilter executed for path: " + path);

        // ============================================================
        // ⭐ ALLOW ALL AUTH ENDPOINTS WITHOUT JWT EXCEPT /logout
        // ============================================================
        if (path.startsWith("/api/v1/auth/")) {

            boolean isProtected =
                    path.equals("/api/v1/auth/logout");  // only logout requires JWT

            if (!isProtected) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ============================================================
        // Extract Authorization header
        // ============================================================
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            // Validate JWT and get username
            String username = jwtService.validateTokenAndRetrieveSubject(token);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Refresh session TTL
                jwtService.refreshSession(username);

                var userDetails = userDetailsService.loadUserByUsername(username);

                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("✔ Authenticated as: " + username);
            }
        }

        chain.doFilter(request, response);
    }
}
