package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService { 

    @Autowired
    private CategoryRepository categoryRepository; 

    public Page<Category> getCategories(PageRequest pageable) { 
        return categoryRepository.findByActiveTrue(pageable);
    } 

    public Optional<Category> getCategoryById(Long categoryId) { 
        return categoryRepository.findById(categoryId)
                .filter(c -> Boolean.TRUE.equals(c.getActive()));
    } 

    public Category createCategory(String description) throws CategoryDuplicateException { 
        List<Category> categories = categoryRepository.findByDescription(description); 
        if (categories.isEmpty())
            return categoryRepository.save(new Category(description)); 
        throw new CategoryDuplicateException(); 
    } 

    @Override
    @Transactional
    public Category updateCategory(Long id, String description) throws CategoryDuplicateException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
        List<Category> existing = categoryRepository.findByDescription(description);
        if (!existing.isEmpty() && !existing.get(0).getId().equals(id)) {
            throw new CategoryDuplicateException();
        }
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
        category.setActive(true);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> getInactiveCategories(PageRequest pageable) { 
        return categoryRepository.findByActiveFalse(pageable);
    }
}
