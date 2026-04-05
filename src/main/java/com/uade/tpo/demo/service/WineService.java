package com.uade.tpo.demo.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.demo.entity.Wine;
import com.uade.tpo.demo.service.payload.WineUpsertInput;

public interface WineService { // Servicio para vinos

    Page<Wine> searchWines(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year,
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId,
            Pageable pageable); // Método para buscar vinos

    Wine getWineById(Long id); // Método para obtener un vino por su id

    Wine createWine(WineUpsertInput input); // Método para crear un vino

    Wine updateWine(Long id, WineUpsertInput input); // Método para actualizar un vino

    void deleteWine(Long id); // Método para eliminar un vino

    Wine updateStock(Long id, int stock); // Método para actualizar el stock de un vino

    Wine updateDiscount(Long id, BigDecimal discountPercent); // Método para actualizar el descuento de un vino
}
