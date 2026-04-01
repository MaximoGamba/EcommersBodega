package com.uade.tpo.demo.service;

import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.controllers.auth.AuthenticationRequest;
import com.uade.tpo.demo.controllers.auth.AuthenticationResponse;
import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.controllers.config.JwtService;
import com.uade.tpo.demo.entity.Role;
import com.uade.tpo.demo.entity.RoleName;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.repository.RoleRepository;
import com.uade.tpo.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service // Indica que la clase es un servicio
@RequiredArgsConstructor // Inyecta las dependencias
public class AuthenticationService {
        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final RoleRepository roleRepository;

        public AuthenticationResponse register(RegisterRequest request) { // Registra un nuevo usuario
                RoleName requestedRole = request.getRole() == null ? RoleName.USER : request.getRole();
                Role role = roleRepository.findByName(requestedRole)
                                .orElseGet(() -> Objects
                                                .requireNonNull(roleRepository
                                                                .save(Role.builder().name(requestedRole).build())));

                var user = User.builder() // Crea un nuevo usuario
                                .firstName(request.getFirstname())
                                .lastName(request.getLastname())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(role)
                                .build();

                repository.save(Objects.requireNonNull(user)); // Guarda el nuevo usuario en la base de datos
                var jwtToken = jwtService.generateToken(user); // Genera un token JWT para el nuevo usuario
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build(); // Construye la respuesta de autenticación
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) { // Autentica un usuario
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword())); // Autentica el usuario con el email y la
                                                                         // contraseña
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow(); // Busca el usuario en la base de datos
                var jwtToken = jwtService.generateToken(user); // Genera un token JWT para el usuario
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build();
        }
}
