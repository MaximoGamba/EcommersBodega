package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Crianza;

@Repository // Indica que la clase es un repositorio
public interface CrianzaRepository extends JpaRepository<Crianza, Long> { // Extiende de JpaRepository para que tenga
                                                                          // los métodos de CRUD

    Optional<Crianza> findByName(String name); // Busca una crianza por su nombre
}
