package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Shipment;
import com.uade.tpo.demo.service.payload.EnvioCreateInput;
import com.uade.tpo.demo.service.payload.EnvioUpdateInput;

public interface ShipmentService { // Servicio para envíos

    Shipment createForOrder(Long orderId, EnvioCreateInput input); // Método para crear un envío para un pedido

    Shipment getById(Long id); // Método para obtener un envío por su id

    Shipment update(Long id, EnvioUpdateInput input); // Método para actualizar un envío
} // Servicio para envíos
