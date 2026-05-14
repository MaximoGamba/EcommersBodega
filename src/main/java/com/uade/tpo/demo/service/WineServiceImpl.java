package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Wine;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.AzucarRepository;
import com.uade.tpo.demo.repository.CepaRepository;
import com.uade.tpo.demo.repository.ColorRepository;
import com.uade.tpo.demo.repository.CrianzaRepository;
import com.uade.tpo.demo.repository.ElaboracionRepository;
import com.uade.tpo.demo.repository.MedidaRepository;
import com.uade.tpo.demo.repository.WineRepository;
import com.uade.tpo.demo.repository.WineSpecification;
import com.uade.tpo.demo.service.payload.WineUpsertInput;

@Service
public class WineServiceImpl implements WineService { 

    @Autowired
    private WineRepository wineRepository; 

    @Autowired
    private ColorRepository colorRepository; 

    @Autowired
    private CepaRepository cepaRepository; 

    @Autowired
    private AzucarRepository azucarRepository; 

    @Autowired
    private CrianzaRepository crianzaRepository; 

    @Autowired
    private ElaboracionRepository elaboracionRepository; 

    @Autowired
    private MedidaRepository medidaRepository; 

    @Override
    @Transactional(readOnly = true)
    public Page<Wine> searchWines(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year,
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId,
            boolean includeInactive, Pageable pageable) {
        Specification<Wine> spec = WineSpecification.withFilters(name, minPrice, maxPrice, year,
                colorId, cepaId, azucarId, crianzaId, elaboracionId, medidaId, includeInactive);
        return wineRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true) 
    public Wine getWineById(Long id) { 
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + id));
        if (!isCallerAdmin() && !Boolean.TRUE.equals(wine.getActive())) {
            throw new ResourceNotFoundException("Vino no encontrado: " + id);
        }
        return wine;
    } 

    private static boolean isCallerAdmin() { 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
        if (auth == null) { 
            return false; 
        }
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())); 
    } 

    @Override
    @Transactional
    public Wine createWine(WineUpsertInput input) { 
        validateCreate(input); 
        Wine wine = Wine.builder()
                .name(input.getName())
                .winery(input.getWinery())
                .year(input.getYear())
                .price(input.getPrice())
                .stock(input.getStock())
                .color(colorRepository.getReferenceById(input.getColorId()))
                .cepa(cepaRepository.getReferenceById(input.getCepaId()))
                .azucar(azucarRepository.getReferenceById(input.getAzucarId()))
                .crianza(crianzaRepository.getReferenceById(input.getCrianzaId()))
                .elaboracion(elaboracionRepository.getReferenceById(input.getElaboracionId()))
                .medida(medidaRepository.getReferenceById(input.getMedidaId()))
                .discountPercent(input.getDiscountPercent() != null ? input.getDiscountPercent() : BigDecimal.ZERO)
                .imageUrl(input.getImageUrl())
                .active(true)
                .build();
        return wineRepository.save(wine); 
    } 

    @Override
    @Transactional
    public Wine updateWine(Long id, WineUpsertInput input) { 
        Wine wine = getWineById(id); 
        if (input.getName() != null) {
            wine.setName(input.getName()); 
        }
        if (input.getWinery() != null) {
            wine.setWinery(input.getWinery()); 
        }
        if (input.getYear() != null) {
            wine.setYear(input.getYear()); 
        }
        if (input.getPrice() != null) {
            wine.setPrice(input.getPrice()); 
        }
        if (input.getStock() != null) {
            wine.setStock(input.getStock()); 
        }
        if (input.getColorId() != null) {
            wine.setColor(colorRepository.getReferenceById(input.getColorId())); 
        }
        if (input.getCepaId() != null) {
            wine.setCepa(cepaRepository.getReferenceById(input.getCepaId())); 
        }
        if (input.getAzucarId() != null) {
            wine.setAzucar(azucarRepository.getReferenceById(input.getAzucarId())); 
        }
        if (input.getCrianzaId() != null) {
            wine.setCrianza(crianzaRepository.getReferenceById(input.getCrianzaId())); 
        }
        if (input.getElaboracionId() != null) {
            wine.setElaboracion(elaboracionRepository.getReferenceById(input.getElaboracionId())); 
        }
        if (input.getMedidaId() != null) {
            wine.setMedida(medidaRepository.getReferenceById(input.getMedidaId())); 
        }
        if (input.getDiscountPercent() != null) {
            wine.setDiscountPercent(input.getDiscountPercent());
        }
        if (input.getImageUrl() != null) {
            wine.setImageUrl(input.getImageUrl());
        }
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public void deleteWine(Long id) { 
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + id));
        wine.setActive(false);
        wine.setStock(0);
        wineRepository.save(wine);
    } 

    @Override
    @Transactional
    public Wine activateWine(Long id) { 
        Wine wine = wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + id));
        wine.setActive(true);
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public Wine updateStock(Long id, int stock) { 
        if (stock < 0) { 
            throw new BadRequestException("El stock no puede ser negativo"); 
                                                                             
        }
        Wine wine = getWineById(id); 
        wine.setStock(stock);
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public Wine updateDiscount(Long id, BigDecimal discountPercent) { 
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0 
                || discountPercent.compareTo(new BigDecimal("100")) > 0) { 
            throw new BadRequestException("El descuento debe estar entre 0 y 100"); 
                                                                                    
        }
        Wine wine = getWineById(id); 
        wine.setDiscountPercent(discountPercent.setScale(2, RoundingMode.HALF_UP)); 
        return wineRepository.save(wine); 
    }

    private void validateCreate(WineUpsertInput input) { 
        if (input.getName() == null || input.getName().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        if (input.getPrice() == null || input.getStock() == null) { 
            throw new BadRequestException("Precio y stock son obligatorios");
        }
        if (input.getColorId() == null || input.getCepaId() == null || input.getAzucarId() == null
                || input.getCrianzaId() == null || input.getElaboracionId() == null || input.getMedidaId() == null) {
            throw new BadRequestException("Todos los IDs de clasificación del vino son obligatorios"); 
                                                                                                       
        }
    } // Método para validar la creación de un vino
}
