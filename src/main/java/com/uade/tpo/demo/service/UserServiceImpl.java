package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.entity.Role;
import com.uade.tpo.demo.entity.RoleName;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.RoleRepository;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.service.payload.UserUpdateInput;

@Service
public class UserServiceImpl implements UserService { 

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private RoleRepository roleRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de contraseñas

    @Override
    @Transactional(readOnly = true) 
    public List<User> getUsers() { 
        return userRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .toList();
    } 

    @Override
    @Transactional(readOnly = true) 
    public User getUserById(Long id) { 
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        }
        return user;
    } 

    @Override
    @Transactional
    public User register(RegisterRequest request) { 
        if (userRepository.findByEmail(request.getEmail()).isPresent()) { 
            throw new BadRequestException("El email ya está registrado");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BadRequestException("El username es obligatorio");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("El username ya está registrado");
        }
        RoleName requestedRole = request.getRole() == null ? RoleName.USER : request.getRole(); 
        Role role = roleRepository.findByName(requestedRole) 
                .orElseGet(() -> Objects.requireNonNull( 
                        roleRepository.save(Role.builder().name(requestedRole).build()))); 

        User user = User.builder()
                .firstName(request.getFirstname()) 
                .lastName(request.getLastname()) 
                .email(request.getEmail()) 
            .username(request.getUsername()) 
                .password(passwordEncoder.encode(request.getPassword())) 
                .role(role) 
                .build(); 
        return userRepository.save(Objects.requireNonNull(user)); 
    }

    @Override
    @Transactional 
    public User updateUser(Long id, UserUpdateInput input) { 
        User user = getUserById(id); 
        if (input.getEmail() != null && !input.getEmail().equals(user.getEmail())) { 
            if (userRepository.findByEmail(input.getEmail()).isPresent()) { 
                throw new BadRequestException("El email ya está en uso");
            }
            user.setEmail(input.getEmail()); 
        }
        if (input.getUsername() != null && !input.getUsername().equals(user.getUsername())) {
            if (input.getUsername().isBlank()) {
                throw new BadRequestException("El username no puede estar vacío");
            }
            if (userRepository.findByUsername(input.getUsername()).isPresent()) {
                throw new BadRequestException("El username ya está en uso");
            }
            user.setUsername(input.getUsername());
        }
        if (input.getFirstName() != null) { 
            user.setFirstName(input.getFirstName()); 
        }
        if (input.getLastName() != null) {
            user.setLastName(input.getLastName()); 
        }
        if (input.getName() != null) { 
            user.setName(input.getName()); 
        }
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            if (input.getCurrentPassword() == null || input.getCurrentPassword().isBlank()) {
                throw new BadRequestException("Debés ingresar tu contraseña actual para cambiarla");
            }
            if (!passwordEncoder.matches(input.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("La contraseña actual es incorrecta");
            }
            user.setPassword(passwordEncoder.encode(input.getPassword()));
        }
        return userRepository.save(user); 
    }

    @Override
    @Transactional
    public void deleteUser(Long id) { 
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    } 

    @Override
    @Transactional
    public void activateUser(Long id) { 
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        user.setActive(true);
        userRepository.save(user);
    }
}
