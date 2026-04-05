
package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;

public interface CategoryService { // Servicio para categorías
    public Page<Category> getCategories(PageRequest pageRequest); // Método para obtener todas las categorías

    public Optional<Category> getCategoryById(Long categoryId); // Método para obtener una categoría por su id

    public Category createCategory(String description) throws CategoryDuplicateException; // Método para crear una
                                                                                          // categoría
}