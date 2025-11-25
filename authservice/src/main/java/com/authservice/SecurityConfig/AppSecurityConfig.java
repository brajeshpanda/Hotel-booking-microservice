package com.authservice.SecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.authservice.Service.CustomerUserDetailsService;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    // ⭐ PUBLIC URLS (NO AUTH REQUIRED)
    private String[] openUrl = {

            // Registration
            "/api/v1/auth/register",
            "/api/v1/auth/register-owner",
            "/api/v1/auth/register-admin",

            // Login / OTP Login
            "/api/v1/auth/login",
            "/api/v1/auth/login/send-otp",
            "/api/v1/auth/login/verify-otp",

            // Forgot / Reset Password
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",

            // Get active token (public)
            "/api/v1/auth/my-token/**",

            // Swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",

            // Actuator
            "/actuator/**"
    };


    private String[] adminAndOwnerOnly = {
            "/api/v1/auth/get-user"
    };

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerUserDetailsService);
        authProvider.setPasswordEncoder(getEncoder());
        return authProvider;
    }

    @Bean
    PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityConfig(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(req -> req

                        // Fully public authentication endpoints
                        .requestMatchers(openUrl).permitAll()

                        // Admin + Owner (secured)
                        .requestMatchers(adminAndOwnerOnly).hasAnyRole("ADMIN", "OWNER")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authProvider())

                // JWT filter works for all secured requests
                .addFilterBefore(jwtFilter, AnonymousAuthenticationFilter.class);

        return http.build();
    }
}
