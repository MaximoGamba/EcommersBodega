package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.User;

@Repository // Indica que la clase es un repositorio
public interface UserRepository extends JpaRepository<User, Long> { // Extiende de JpaRepository para que tenga los
                                                                    // métodos de CRUD
    Optional<User> findByEmail(String mail); // Busca un usuario por su email
}
