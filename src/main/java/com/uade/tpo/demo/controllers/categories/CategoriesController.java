package com.uade.tpo.demo.controllers.categories;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.service.CategoryService;
import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(page, size)));
    }

    @GetMapping("/inactivas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Category>> getInactiveCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(categoryService.getInactiveCategories(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(categoryService.getInactiveCategories(PageRequest.of(page, size)));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Category result = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con id " + categoryId));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createCategory(@RequestBody CategoryRequest categoryRequest)
            throws CategoryDuplicateException {
        Category result = categoryService.createCategory(categoryRequest.getDescription());
        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId,
            @RequestBody CategoryRequest categoryRequest) throws CategoryDuplicateException {
        Category result = categoryService.updateCategory(categoryId, categoryRequest.getDescription());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{categoryId}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(Map.of("message", "Categoría eliminada correctamente"));
    }

    @PutMapping("/{categoryId}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateCategory(@PathVariable Long categoryId) {
        categoryService.activateCategory(categoryId);
        return ResponseEntity.ok(Map.of("message", "Categoría activada correctamente"));
    }
}
