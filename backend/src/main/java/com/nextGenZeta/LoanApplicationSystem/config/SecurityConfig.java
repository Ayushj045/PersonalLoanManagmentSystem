package com.nextGenZeta.LoanApplicationSystem.config;

import com.nextGenZeta.LoanApplicationSystem.filters.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.nextGenZeta.LoanApplicationSystem.filters.JWTFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(JWTFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    // backend/insurance/src/main/java/com/innov8ors/insurance/config/SecurityConfig.java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                            corsConfig.setAllowedOrigins(List.of("http://localhost:5173"));
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            corsConfig.setAllowedHeaders(List.of("*"));
                            corsConfig.setAllowCredentials(true);
                            return corsConfig;
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/emi/preview").permitAll()
                        .requestMatchers("/api/v1/auth/update-password/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/api/v1/admin/loans/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/loans/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/support/user/**").hasAuthority("USER")
                        .requestMatchers("/api/v1/support/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/loans/**", "/api/emi/**").hasAuthority("USER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}