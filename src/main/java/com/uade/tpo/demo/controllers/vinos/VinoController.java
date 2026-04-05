package com.uade.tpo.demo.controllers.vinos;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Wine;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.service.WineService;
import com.uade.tpo.demo.service.payload.WineUpsertInput;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vinos")
@RequiredArgsConstructor
public class VinoController {

    private final WineService wineService;

    @GetMapping
    public Page<Wine> listarConFiltros(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long colorId,
            @RequestParam(required = false) Long cepaId,
            @RequestParam(required = false) Long azucarId,
            @RequestParam(required = false) Long crianzaId,
            @RequestParam(required = false) Long elaboracionId,
            @RequestParam(required = false) Long medidaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return wineService.searchWines(name, minPrice, maxPrice, year, colorId, cepaId, azucarId, crianzaId,
                elaboracionId, medidaId, pageable);
    }

    @GetMapping("/{id}")
    public Wine obtener(@PathVariable Long id) {
        return wineService.getWineById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Wine> crear(@RequestBody VinoRequest request) {
        Wine created = wineService.createWine(toUpsertInput(request));
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Wine modificar(@PathVariable Long id, @RequestBody VinoRequest request) {
        return wineService.updateWine(id, toUpsertInput(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        wineService.deleteWine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Wine actualizarStock(@PathVariable Long id, @RequestBody VinoStockRequest request) {
        if (request.getStock() == null) {
            throw new BadRequestException("stock es obligatorio");
        }
        return wineService.updateStock(id, request.getStock());
    }

    @PutMapping("/{id}/descuento")
    @PreAuthorize("hasRole('ADMIN')")
    public Wine actualizarDescuento(@PathVariable Long id, @RequestBody VinoDescuentoRequest request) {
        if (request.getDiscountPercent() == null) {
            throw new BadRequestException("discountPercent es obligatorio");
        }
        return wineService.updateDiscount(id, request.getDiscountPercent());
    }

    private static WineUpsertInput toUpsertInput(VinoRequest request) {
        WineUpsertInput input = new WineUpsertInput();
        BeanUtils.copyProperties(request, input);
        return input;
    }
}
