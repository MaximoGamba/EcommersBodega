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
public class ShipmentServiceImpl implements ShipmentService { 

    @Autowired
    private ShipmentRepository shipmentRepository; 

    @Autowired
    private OrderRepository orderRepository; 

    @Override // Override del método para crear un envío para un pedido
    @Transactional // Transacción para crear un envío para un pedido
    public Shipment createForOrder(Long orderId, EnvioCreateInput input) { 
        Order order = orderRepository.findById(orderId) 
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + orderId));
        if (shipmentRepository.findByOrder_Id(orderId).isPresent()) { 
            throw new BadRequestException("El pedido ya tiene un envío registrado");
        }
        if (input.getAddress() == null || input.getAddress().isBlank()) { 
            throw new BadRequestException("La dirección es obligatoria");
        }
        Shipment shipment = Shipment.builder() 
                .order(order)
                .address(input.getAddress())
                .status(ShipmentStatus.PENDING)
                .trackingNumber(input.getTrackingNumber())
                .createdAt(LocalDateTime.now())
                .build();
        shipment = shipmentRepository.save(shipment); 
        order.setShipment(shipment);
        orderRepository.save(order); 
        return shipment; 
    } 

    @Override 
    @Transactional(readOnly = true)
    public Shipment getById(Long id) { 
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Shipment> getByOrderId(Long orderId) { 
        return shipmentRepository.findByOrder_Id(orderId);
    }

    @Override 
    @Transactional
    public Shipment update(Long id, EnvioUpdateInput input) { 
        Shipment shipment = getById(id); 
        if (input == null) { 
            return shipment;
        }
        if (input.getAddress() != null) { 
            shipment.setAddress(input.getAddress());
        }
        if (input.getStatus() != null) { 
            shipment.setStatus(input.getStatus());
        }
        if (input.getTrackingNumber() != null) { 
            shipment.setTrackingNumber(input.getTrackingNumber());
        }
        return shipmentRepository.save(shipment); 
    } 
} 
