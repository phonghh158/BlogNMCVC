package com.J2EE.BlogNMCVC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Test HTTP CLient
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/auth/register"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register",
                                "/api/auth/verify")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(withDefaults());

        return http.build();
    }

    // Sử dụng form thymeleaf
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/auth/register").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(withDefaults())
//                .logout(withDefaults());
//
//        return http.build();
//    }
}