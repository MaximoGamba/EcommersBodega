package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
public class WineServiceImpl implements WineService { // Implementación del servicio para vinos

    @Autowired
    private WineRepository wineRepository; // Repositorio para vinos

    @Autowired
    private ColorRepository colorRepository; // Repositorio para colores

    @Autowired
    private CepaRepository cepaRepository; // Repositorio para cepas

    @Autowired
    private AzucarRepository azucarRepository; // Repositorio para azucares

    @Autowired
    private CrianzaRepository crianzaRepository; // Repositorio para crianzas

    @Autowired
    private ElaboracionRepository elaboracionRepository; // Repositorio para elaboraciones

    @Autowired
    private MedidaRepository medidaRepository; // Repositorio para medidas

    @Override
    @Transactional(readOnly = true) // Transacción para buscar vinos
    public Page<Wine> searchWines(String name, BigDecimal minPrice, BigDecimal maxPrice, Integer year,
            Long colorId, Long cepaId, Long azucarId, Long crianzaId, Long elaboracionId, Long medidaId,
            Pageable pageable) { // Método para buscar vinos
        Specification<Wine> spec = WineSpecification.withFilters(name, minPrice, maxPrice, year,
                colorId, cepaId, azucarId, crianzaId, elaboracionId, medidaId);
        return wineRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true) // Transacción para obtener un vino por su id
    public Wine getWineById(Long id) { // Método para obtener un vino por su id
        return wineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vino no encontrado: " + id));
    } // Método para obtener un vino por su id

    @Override
    @Transactional
    public Wine createWine(WineUpsertInput input) { // Método para crear un vino
        validateCreate(input);
        Wine wine = Wine.builder() // Crea un nuevo vino
                .name(input.getName())
                .winery(input.getWinery()) // Establece el nombre de la bodega
                .year(input.getYear())
                .price(input.getPrice()) // Establece el precio del vino
                .stock(input.getStock()) // Establece el stock del vino
                .color(colorRepository.getReferenceById(input.getColorId()))
                .cepa(cepaRepository.getReferenceById(input.getCepaId())) // Establece la cepa del vino
                .azucar(azucarRepository.getReferenceById(input.getAzucarId())) // Establece el azúcar del vino
                .crianza(crianzaRepository.getReferenceById(input.getCrianzaId())) // Establece la crianza del vino
                .elaboracion(elaboracionRepository.getReferenceById(input.getElaboracionId()))
                .medida(medidaRepository.getReferenceById(input.getMedidaId()))
                .discountPercent(input.getDiscountPercent() != null ? input.getDiscountPercent() : BigDecimal.ZERO)
                .build();
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public Wine updateWine(Long id, WineUpsertInput input) { // Método para actualizar un vino
        Wine wine = getWineById(id); // Obtiene el vino por su id
        if (input.getName() != null) {
            wine.setName(input.getName()); // Establece el nombre del vino
        }
        if (input.getWinery() != null) {
            wine.setWinery(input.getWinery()); // Establece el nombre de la bodega
        }
        if (input.getYear() != null) {
            wine.setYear(input.getYear()); // Establece el año del vino
        }
        if (input.getPrice() != null) {
            wine.setPrice(input.getPrice()); // Establece el precio del vino
        }
        if (input.getStock() != null) {
            wine.setStock(input.getStock()); // Establece el stock del vino
        }
        if (input.getColorId() != null) {
            wine.setColor(colorRepository.getReferenceById(input.getColorId())); // Establece el color del vino
        }
        if (input.getCepaId() != null) {
            wine.setCepa(cepaRepository.getReferenceById(input.getCepaId())); // Establece la cepa del vino
        }
        if (input.getAzucarId() != null) {
            wine.setAzucar(azucarRepository.getReferenceById(input.getAzucarId())); // Establece el azúcar del vino
        }
        if (input.getCrianzaId() != null) {
            wine.setCrianza(crianzaRepository.getReferenceById(input.getCrianzaId())); // Establece la crianza del vino
        }
        if (input.getElaboracionId() != null) {
            wine.setElaboracion(elaboracionRepository.getReferenceById(input.getElaboracionId())); // Establece la
                                                                                                   // elaboración del
                                                                                                   // vino
        }
        if (input.getMedidaId() != null) {
            wine.setMedida(medidaRepository.getReferenceById(input.getMedidaId())); // Establece la medida del vino
        }
        if (input.getDiscountPercent() != null) {
            wine.setDiscountPercent(input.getDiscountPercent()); // Establece el descuento del vino
        }
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public void deleteWine(Long id) { // Método para eliminar un vino
        if (!wineRepository.existsById(id)) { // Si el vino no existe
            throw new ResourceNotFoundException("Vino no encontrado: " + id); // Lanza una excepción si el vino no
                                                                              // existe
        }
        wineRepository.deleteById(id); // Elimina el vino
    } // Método para eliminar un vino

    @Override
    @Transactional
    public Wine updateStock(Long id, int stock) { // Método para actualizar el stock de un vino
        if (stock < 0) { // Si el stock es negativo
            throw new BadRequestException("El stock no puede ser negativo"); // Lanza una excepción si el stock es
                                                                             // negativo
        }
        Wine wine = getWineById(id); // Obtiene el vino por su id
        wine.setStock(stock);
        return wineRepository.save(wine);
    }

    @Override
    @Transactional
    public Wine updateDiscount(Long id, BigDecimal discountPercent) { // Método para actualizar el descuento de un vino
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0 // Si el descuento es nulo o es
                                                                                      // menor a 0
                || discountPercent.compareTo(new BigDecimal("100")) > 0) { // Si el descuento es mayor a 100
            throw new BadRequestException("El descuento debe estar entre 0 y 100"); // Lanza una excepción si el
                                                                                    // descuento es menor a 0 o mayor a
                                                                                    // 100
        }
        Wine wine = getWineById(id); // Obtiene el vino por su id
        wine.setDiscountPercent(discountPercent.setScale(2, RoundingMode.HALF_UP)); // Establece el descuento del vino
        return wineRepository.save(wine); // Guarda el vino
    }

    private void validateCreate(WineUpsertInput input) { // Método para validar la creación de un vino
        if (input.getName() == null || input.getName().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        if (input.getPrice() == null || input.getStock() == null) { // Si el precio o el stock es nulo
            throw new BadRequestException("Precio y stock son obligatorios");
        }
        if (input.getColorId() == null || input.getCepaId() == null || input.getAzucarId() == null
                || input.getCrianzaId() == null || input.getElaboracionId() == null || input.getMedidaId() == null) {
            throw new BadRequestException("Todos los IDs de clasificación del vino son obligatorios"); // Lanza una
                                                                                                       // excepción si
                                                                                                       // los IDs de
                                                                                                       // clasificación
                                                                                                       // del vino son
                                                                                                       // nulos
        }
    } // Método para validar la creación de un vino
}
