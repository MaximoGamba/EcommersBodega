package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Cepa;

@Repository // Indica que la clase es un repositorio
public interface CepaRepository extends JpaRepository<Cepa, Long> { // Extiende de JpaRepository para que tenga los
                                                                    // métodos de CRUD

    Optional<Cepa> findByName(String name); // Busca una cepa por su nombre
}
