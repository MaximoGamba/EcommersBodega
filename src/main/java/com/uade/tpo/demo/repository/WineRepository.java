package com.uade.tpo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Wine;

@Repository // Indica que la clase es un repositorio
public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> { // Extiende de
                                                                                                    // JpaRepository
                                                                                                    // para que tenga
                                                                                                    // los métodos de
                                                                                                    // CRUD y
                                                                                                    // JpaSpecificationExecutor
                                                                                                    // para que tenga
                                                                                                    // los métodos de
                                                                                                    // búsqueda
}
