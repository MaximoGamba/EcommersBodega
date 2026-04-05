package com.uade.tpo.demo.controllers.usuarios;

import com.uade.tpo.demo.entity.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String name;
    private RoleName role;
}
