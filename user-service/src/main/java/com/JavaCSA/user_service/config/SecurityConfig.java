package com.JavaCSA.user_service.config;

import com.JavaCSA.user_service.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    /**
     * Configures the security filter chain that applies to all HTTP requests.
     *
     * @param http HttpSecurity object provided by Spring Security to configure web-based security.
     * @return the built SecurityFilterChain which holds the configuration of how security is managed.
     * @throws Exception if an error occurs during the configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session policy to stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll() // Permit access to login and register endpoints
                        .anyRequest().authenticated()) // All other requests require authentication
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add custom JWT filter

        return http.build(); // Build and return the configured SecurityFilterChain
    }
}
