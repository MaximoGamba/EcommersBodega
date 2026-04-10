package com.uade.tpo.demo.controllers.auth;

import com.uade.tpo.demo.entity.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private RoleName role;
}
//dto de registro, es lo q cargo 
