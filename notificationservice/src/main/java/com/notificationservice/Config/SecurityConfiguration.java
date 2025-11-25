package com.notificationservice.Config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityConfig(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Only ADMIN users allowed
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Everything else allow without authentication
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

