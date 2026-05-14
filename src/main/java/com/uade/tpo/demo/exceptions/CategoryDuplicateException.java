package com.uade.tpo.demo.exceptions;

public class CategoryDuplicateException extends Exception {

    public CategoryDuplicateException() {
        super("La categoría que se intenta agregar ya existe");
    }
}
