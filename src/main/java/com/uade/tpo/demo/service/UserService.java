package com.uade.tpo.demo.service;

import java.util.List;

import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.service.payload.UserUpdateInput;

public interface UserService { 

    List<User> getUsers(); 

    User getUserById(Long id); 

    User register(RegisterRequest request); 

    User updateUser(Long id, UserUpdateInput input); 

    void deleteUser(Long id); 

    void activateUser(Long id); 
}
