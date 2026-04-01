package com.uade.tpo.demo.controllers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration // Configura la seguridad de la aplicación
@EnableWebSecurity // Habilita la seguridad web
@RequiredArgsConstructor // Inyecta las dependencias
public class SecurityConfig { // Clase de configuración de la seguridad

        private final JwtAuthenticationFilter jwtAuthFilter; // Filtro de autenticación por JWT
        private final AuthenticationProvider authenticationProvider; // Proveedor de autenticación

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Filtro de seguridad
                http
                                .csrf(AbstractHttpConfigurer::disable) // Deshabilita el CSRF
                                .authorizeHttpRequests(req -> req.requestMatchers("/api/v1/auth/**")
                                                .permitAll() // Permite el acceso a la ruta /api/v1/auth/**
                                                .anyRequest() // Cualquier otra ruta requiere autenticación
                                                .authenticated()) // Requiere autenticación
                                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS)) // No se crea
                                                                                                        // una sesión
                                                                                                        // estática
                                .authenticationProvider(authenticationProvider) // Proveedor de autenticación
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Filtro
                                                                                                             // de
                                                                                                             // autenticación
                                                                                                             // por JWT

                return http.build(); // Construye el filtro de seguridad y lo devuelve
        }
}
