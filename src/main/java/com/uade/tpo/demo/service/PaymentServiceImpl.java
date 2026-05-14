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
public class PaymentServiceImpl implements PaymentService { 

    @Autowired 
    private PaymentRepository paymentRepository; 

    @Autowired 
    private OrderRepository orderRepository; 

    @Override 
    @Transactional 
    public Payment createForOrder(Long orderId, PagoCreateInput input) { 
        Order order = orderRepository.findById(orderId) 
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + orderId));
        if (paymentRepository.findByOrder_Id(orderId).isPresent()) { 
            throw new BadRequestException("El pedido ya tiene un pago registrado");
        }
        if (input.getAmount() == null || input.getAmount().signum() <= 0) { 
            throw new BadRequestException("El monto debe ser mayor a 0");
        }
        Payment payment = Payment.builder() 
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
    public Payment getById(Long id) { 
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    } 

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Payment> getByOrderId(Long orderId) { 
        return paymentRepository.findByOrder_Id(orderId);
    }

    @Override 
    @Transactional 
    public Payment update(Long id, PagoUpdateInput input) { 
        Payment payment = getById(id); 
        if (input == null) { 
            return payment;
        }
        if (input.getStatus() != null) { 
            payment.setStatus(input.getStatus());
        }
        if (input.getAmount() != null) { 
            payment.setAmount(input.getAmount());
        }
        return paymentRepository.save(payment);
    }
}
