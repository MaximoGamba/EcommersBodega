package com.uade.tpo.demo.controllers.usuarios;

import lombok.Data;

@Data
public class UsuarioUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private String currentPassword;
    private String password;
}
