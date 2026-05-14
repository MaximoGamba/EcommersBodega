
package com.uade.tpo.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;

public interface CategoryService { 
    public Page<Category> getCategories(PageRequest pageRequest); 

    public Optional<Category> getCategoryById(Long categoryId); 

    public Category createCategory(String description) throws CategoryDuplicateException; 

    Category updateCategory(Long id, String description) throws CategoryDuplicateException; 

    void deleteCategory(Long id); // Método para desactivar una categoría (soft delete)

    void activateCategory(Long id); // Método para activar una categoría desactivada

    Page<Category> getInactiveCategories(PageRequest pageRequest); // Método para obtener las categorías inactivas
}