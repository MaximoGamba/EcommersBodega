package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.service.payload.UserUpdateInput;

public interface UserService { // Servicio para usuarios

    List<User> getUsers(); // Método para obtener todos los usuarios

    User getUserById(Long id); // Método para obtener un usuario por su id

    User register(RegisterRequest request); // Método para registrar un usuario

    User updateUser(Long id, UserUpdateInput input); // Método para actualizar un usuario

    void deleteUser(Long id); // Método para eliminar un usuario
}
