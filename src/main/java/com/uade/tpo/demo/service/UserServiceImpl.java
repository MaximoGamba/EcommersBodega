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
public class UserServiceImpl implements UserService { // Implementación del servicio para usuarios

    @Autowired
    private UserRepository userRepository; // Repositorio para usuarios

    @Autowired
    private RoleRepository roleRepository; // Repositorio para roles

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de contraseñas

    @Override
    @Transactional(readOnly = true) // Transacción para obtener todos los usuarios
    public List<User> getUsers() { // Método para obtener todos los usuarios
        return userRepository.findAll();
    } // Método para obtener todos los usuarios

    @Override
    @Transactional(readOnly = true) // Transacción para obtener un usuario por su id
    public User getUserById(Long id) { // Método para obtener un usuario por su id
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    } // Método para obtener un usuario por su id

    @Override
    @Transactional
    public User register(RegisterRequest request) { // Método para registrar un usuario
        if (userRepository.findByEmail(request.getEmail()).isPresent()) { // Si el email ya está registrado
            throw new BadRequestException("El email ya está registrado");
        }
        RoleName requestedRole = request.getRole() == null ? RoleName.USER : request.getRole(); // Si el rol es nulo
                                                                                                // establece el rol de
                                                                                                // usuario
        Role role = roleRepository.findByName(requestedRole) // Busca un rol por su nombre
                .orElseGet(() -> Objects.requireNonNull(
                        roleRepository.save(Role.builder().name(requestedRole).build()))); // Si el rol no existe lo
                                                                                           // crea

        User user = User.builder() // Crea un nuevo usuario
                .firstName(request.getFirstname())
                .lastName(request.getLastname()) // Establece el apellido del usuario
                .email(request.getEmail()) // Establece el email del usuario
                .password(passwordEncoder.encode(request.getPassword())) // Establece la contraseña del usuario
                .role(role)
                .build();
        return userRepository.save(Objects.requireNonNull(user)); // Guarda el usuario
    }

    @Override
    @Transactional // Transacción para actualizar un usuario
    public User updateUser(Long id, UserUpdateInput input) { // Método para actualizar un usuario
        User user = getUserById(id); // Obtiene el usuario por su id
        if (input.getEmail() != null && !input.getEmail().equals(user.getEmail())) { // Si el email es diferente al
                                                                                     // email del usuario
            if (userRepository.findByEmail(input.getEmail()).isPresent()) { // Si el email ya está en uso
                throw new BadRequestException("El email ya está en uso");
            }
            user.setEmail(input.getEmail()); // Establece el email del usuario
        }
        if (input.getFirstName() != null) { // Si el nombre es nulo establece el nombre del usuario
            user.setFirstName(input.getFirstName()); // Establece el nombre del usuario
        }
        if (input.getLastName() != null) {
            user.setLastName(input.getLastName()); // Establece el apellido del usuario
        }
        if (input.getName() != null) { // Si el nombre es nulo establece el nombre del usuario
            user.setName(input.getName()); // Establece el nombre del usuario
        }
        if (input.getPassword() != null && !input.getPassword().isBlank()) { // Si la contraseña es nula o en blanco
                                                                             // establece la contraseña del usuario
            user.setPassword(passwordEncoder.encode(input.getPassword())); // Establece la contraseña del usuario
        }
        return userRepository.save(user); // Guarda el usuario
    }

    @Override
    @Transactional
    public void deleteUser(Long id) { // Método para eliminar un usuario
        if (!userRepository.existsById(id)) { // Si el usuario no existe
            throw new ResourceNotFoundException("Usuario no encontrado: " + id); // Lanza una excepción si el usuario no
                                                                                 // existe
        }
        userRepository.deleteById(id); // Elimina el usuario
    } // Método para eliminar un usuario
}
