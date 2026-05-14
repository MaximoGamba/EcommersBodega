package com.uade.tpo.demo.service;

import com.uade.tpo.demo.entity.Payment;
import com.uade.tpo.demo.service.payload.PagoCreateInput;
import com.uade.tpo.demo.service.payload.PagoUpdateInput;

public interface PaymentService { 

    Payment createForOrder(Long orderId, PagoCreateInput input); 

    Payment getById(Long id); 

    Payment update(Long id, PagoUpdateInput input); 

    java.util.Optional<Payment> getByOrderId(Long orderId); 
} 
