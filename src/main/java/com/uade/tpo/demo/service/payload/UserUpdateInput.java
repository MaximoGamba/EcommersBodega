package com.uade.tpo.demo.service.payload;

import lombok.Data;

@Data
public class UserUpdateInput {
    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private String password;
}
