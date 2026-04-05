package com.uade.tpo.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.Category;

@Repository // Indica que la clase es un repositorio
public interface CategoryRepository extends JpaRepository<Category, Long> { // Extiende de JpaRepository para que tenga
                                                                            // los métodos de CRUD

    @Query(value = "select c from Category c where c.description = ?1") // Busca una categoría por su descripción
    List<Category> findByDescription(String description); // Busca una categoría por su descripción
}
