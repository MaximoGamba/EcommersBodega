package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Shipment;
import com.uade.tpo.demo.service.payload.EnvioCreateInput;
import com.uade.tpo.demo.service.payload.EnvioUpdateInput;

public interface ShipmentService { 

    Shipment createForOrder(Long orderId, EnvioCreateInput input); 

    Shipment getById(Long id); 

    Shipment update(Long id, EnvioUpdateInput input); 

    java.util.Optional<Shipment> getByOrderId(Long orderId); 
} 
