package com.uade.tpo.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.Payment;
import com.uade.tpo.demo.entity.PaymentStatus;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.OrderRepository;
import com.uade.tpo.demo.repository.PaymentRepository;
import com.uade.tpo.demo.service.payload.PagoCreateInput;
import com.uade.tpo.demo.service.payload.PagoUpdateInput;

@Service
public class PaymentServiceImpl implements PaymentService { // Implementación del servicio para pagos

    @Autowired // Inyección de dependencias
    private PaymentRepository paymentRepository; // Repositorio para pagos

    @Autowired // Inyección de dependencias
    private OrderRepository orderRepository; // Repositorio para pedidos

    @Override // Override del método para crear un pago para un pedido
    @Transactional // Transacción para crear un pago para un pedido
    public Payment createForOrder(Long orderId, PagoCreateInput input) { // Método para crear un pago para un pedido
        Order order = orderRepository.findById(orderId) // Obtiene el pedido por su id
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + orderId));
        if (paymentRepository.findByOrder_Id(orderId).isPresent()) { // Si el pedido ya tiene un pago registrado
            throw new BadRequestException("El pedido ya tiene un pago registrado");
        }
        if (input.getAmount() == null || input.getAmount().signum() <= 0) { // Si el monto es nulo o menor a 0
            throw new BadRequestException("El monto debe ser mayor a 0");
        }
        Payment payment = Payment.builder() // Crea un nuevo pago
                .order(order)
                .amount(input.getAmount())
                .status(PaymentStatus.PENDING)
                .method(input.getMethod())
                .createdAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getById(Long id) { // Método para obtener un pago por su id
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    } // Método para obtener un pago por su id

    @Override // Override del método para obtener un pago por su id
    @Transactional // Transacción para actualizar un pago
    public Payment update(Long id, PagoUpdateInput input) { // Método para actualizar un pago
        Payment payment = getById(id); // Obtiene el pago por su id
        if (input == null) { // Si el input es nulo retorna el pago
            return payment;
        }
        if (input.getStatus() != null) { // Si el estado es nulo establece el estado del pago
            payment.setStatus(input.getStatus());
        }
        if (input.getAmount() != null) { // Si el monto es nulo establece el monto del pago
            payment.setAmount(input.getAmount());
        }
        return paymentRepository.save(payment);
    }
}
