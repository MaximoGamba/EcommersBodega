package com.uade.tpo.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.Shipment;
import com.uade.tpo.demo.entity.ShipmentStatus;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.ShipmentRepository;
import com.uade.tpo.demo.service.payload.EnvioCreateInput;
import com.uade.tpo.demo.service.payload.EnvioUpdateInput;

@Service
public class ShipmentServiceImpl implements ShipmentService { // Implementación del servicio para envíos

    @Autowired
    private ShipmentRepository shipmentRepository; // Repositorio para envíos

    @Autowired
    private OrderRepository orderRepository; // Repositorio para pedidos

    @Override // Override del método para crear un envío para un pedido
    @Transactional // Transacción para crear un envío para un pedido
    public Shipment createForOrder(Long orderId, EnvioCreateInput input) { // Método para crear un envío para un pedido
        Order order = orderRepository.findById(orderId) // Obtiene el pedido por su id
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + orderId));
        if (shipmentRepository.findByOrder_Id(orderId).isPresent()) { // Si el pedido ya tiene un envío registrado
            throw new BadRequestException("El pedido ya tiene un envío registrado");
        }
        if (input.getAddress() == null || input.getAddress().isBlank()) { // Si la dirección es nula o en blanco
            throw new BadRequestException("La dirección es obligatoria");
        }
        Shipment shipment = Shipment.builder() // Crea un nuevo envío
                .order(order)
                .address(input.getAddress())
                .status(ShipmentStatus.PENDING)
                .trackingNumber(input.getTrackingNumber())
                .createdAt(LocalDateTime.now())
                .build();
        shipment = shipmentRepository.save(shipment); // Guarda el envío
        order.setShipment(shipment);
        orderRepository.save(order); // Guarda el pedido
        return shipment; // Retorna el envío
    } // Método para crear un envío para un pedido

    @Override // Override del método para obtener un envío por su id
    @Transactional(readOnly = true)
    public Shipment getById(Long id) { // Método para obtener un envío por su id
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + id));
    }

    @Override // Override del método para actualizar un envío
    @Transactional
    public Shipment update(Long id, EnvioUpdateInput input) { // Método para actualizar un envío
        Shipment shipment = getById(id); // Obtiene el envío por su id
        if (input == null) { // Si el input es nulo retorna el envío
            return shipment;
        }
        if (input.getAddress() != null) { // Si la dirección es nula establece la dirección del envío
            shipment.setAddress(input.getAddress());
        }
        if (input.getStatus() != null) { // Si el estado es nulo establece el estado del envío
            shipment.setStatus(input.getStatus());
        }
        if (input.getTrackingNumber() != null) { // Si el número de tracking es nulo establece el número de tracking del
                                                 // envío
            shipment.setTrackingNumber(input.getTrackingNumber());
        }
        return shipmentRepository.save(shipment); // Guarda el envío
    } // Método para actualizar un envío
} // Implementación del servicio para envíos
