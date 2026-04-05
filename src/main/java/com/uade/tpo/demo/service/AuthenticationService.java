package com.uade.tpo.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.controllers.auth.AuthenticationRequest;
import com.uade.tpo.demo.controllers.auth.AuthenticationResponse;
import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class AuthenticationService { // Servicio para autenticar usuarios

        @Autowired
        private UserRepository userRepository; // Repositorio para usuarios

        @Autowired
        private JwtService jwtService; // Servicio para generar tokens JWT

        @Autowired
        private AuthenticationManager authenticationManager; // Gestor de autenticación

        @Autowired
        private UserService userService; // Servicio para usuarios

        public AuthenticationResponse register(RegisterRequest request) { // Método para registrar un usuario
                User user = userService.register(request); // Registra un usuario
                String jwtToken = jwtService.generateToken(user); // Genera un token JWT
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken) // Genera un token JWT
                                .build(); // Genera un token JWT
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) { // Método para autenticar un usuario
                                                                                    // con email y contraseña
                authenticationManager.authenticate( // Autentica un usuario con email y contraseña
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                User user = userRepository.findByEmail(request.getEmail()) // Busca un usuario por email
                                .orElseThrow(); // Lanza una excepción si el usuario no existe
                String jwtToken = jwtService.generateToken(user); // Genera un token JWT
                return AuthenticationResponse.builder() // Genera un token JWT
                                .accessToken(jwtToken) // Genera un token JWT
                                .build(); // Genera un token JWT
        }
}
