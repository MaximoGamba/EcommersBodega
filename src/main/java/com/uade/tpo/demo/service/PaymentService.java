package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Payment;
import com.uade.tpo.demo.service.payload.PagoCreateInput;
import com.uade.tpo.demo.service.payload.PagoUpdateInput;

public interface PaymentService { // Servicio para pagos

    Payment createForOrder(Long orderId, PagoCreateInput input); // Método para crear un pago para un pedido

    Payment getById(Long id); // Método para obtener un pago por su id

    Payment update(Long id, PagoUpdateInput input); // Método para actualizar un pago
} // Servicio para pagos
