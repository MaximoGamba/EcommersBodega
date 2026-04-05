package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Elaboracion;

@Repository // Indica que la clase es un repositorio
public interface ElaboracionRepository extends JpaRepository<Elaboracion, Long> { // Extiende de JpaRepository para que
                                                                                  // tenga los métodos de CRUD

    Optional<Elaboracion> findByName(String name); // Busca una elaboración por su nombre
}
