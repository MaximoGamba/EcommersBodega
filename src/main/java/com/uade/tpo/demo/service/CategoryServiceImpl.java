package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService { // Implementación del servicio para categorías

    @Autowired
    private CategoryRepository categoryRepository; // Repositorio para categorías

    public Page<Category> getCategories(PageRequest pageable) { // Método para obtener todas las categorías
        return categoryRepository.findAll(pageable);
    } // Método para obtener todas las categorías

    public Optional<Category> getCategoryById(Long categoryId) { // Método para obtener una categoría por su id
        return categoryRepository.findById(categoryId);
    } // Método para obtener una categoría por su id

    public Category createCategory(String description) throws CategoryDuplicateException { // Método para crear una
                                                                                           // categoría
        List<Category> categories = categoryRepository.findByDescription(description); // Busca una categoría por su
                                                                                       // descripción
        if (categories.isEmpty())
            return categoryRepository.save(new Category(description)); // Guarda una categoría
        throw new CategoryDuplicateException(); // Lanza una excepción si la categoría ya existe
    } // Método para crear una categoría
}
