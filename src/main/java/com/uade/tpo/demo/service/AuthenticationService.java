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
public class AuthenticationService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserService userService;

        public AuthenticationResponse register(RegisterRequest request) {
                User user = userService.register(request);
                user = rotateTokenVersion(user);
                String jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .userId(user.getId())
                                .role(user.getRole().getName().name())
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                User user = userRepository.findByUsernameAndActiveTrue(request.getUsername())
                                .orElseThrow();
                user = rotateTokenVersion(user);
                String jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .userId(user.getId())
                                .role(user.getRole().getName().name())
                                .build();
        }

        private User rotateTokenVersion(User user) {
                int currentVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
                user.setTokenVersion(currentVersion + 1);
                return userRepository.save(user);
        }
}
