package com.uade.tpo.demo.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.demo.entity.Wine;
import com.uade.tpo.demo.service.payload.WineUpsertInput;

public interface WineService { 

    Page<Wine> searchWines(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year,
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId,
            boolean includeInactive, Pageable pageable);

    Wine getWineById(Long id); 

    Wine createWine(WineUpsertInput input); 

    Wine updateWine(Long id, WineUpsertInput input); 

    void deleteWine(Long id); 

    Wine activateWine(Long id); 

    Wine updateStock(Long id, int stock); 

    Wine updateDiscount(Long id, BigDecimal discountPercent); 
}
