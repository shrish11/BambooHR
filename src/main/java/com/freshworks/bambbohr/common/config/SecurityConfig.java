package com.freshworks.bambbohr.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/prometheus", "/actuator/health").permitAll()  // Allow Prometheus access
                        .anyRequest().authenticated()  // Protect other endpoints
                )
                .httpBasic()  // Enable basic authentication for secured endpoints
                .and()
                .csrf().disable();  // Disable CSRF if not needed
        return http.build();
    }
}
