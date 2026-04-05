package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Role;
import com.uade.tpo.demo.entity.RoleName;

@Repository // Indica que la clase es un repositorio
public interface RoleRepository extends JpaRepository<Role, Long> { // Extiende de JpaRepository para que tenga los
                                                                    // métodos de CRUD
    Optional<Role> findByName(RoleName name); // Busca un rol por su nombre
}
