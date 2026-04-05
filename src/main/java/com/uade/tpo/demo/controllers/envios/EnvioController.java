package com.uade.tpo.demo.controllers.envios;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Shipment;
import com.uade.tpo.demo.service.ShipmentService;
import com.uade.tpo.demo.service.payload.EnvioUpdateInput;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final ShipmentService shipmentService;

    @GetMapping("/{id}")
    @PreAuthorize("@shipmentAccess.canAccess(authentication, #id)")
    public Shipment obtener(@PathVariable Long id) {
        return shipmentService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@shipmentAccess.canAccess(authentication, #id)")
    public Shipment actualizar(@PathVariable Long id, @RequestBody(required = false) EnvioUpdateInput input) {
        return shipmentService.update(id, input);
    }
}
