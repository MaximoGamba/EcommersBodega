package com.uade.tpo.demo.service.payload;

import java.math.BigDecimal;

import com.uade.tpo.demo.entity.PaymentStatus;

import lombok.Data;

@Data
public class PagoUpdateInput {
    private PaymentStatus status;
    private BigDecimal amount;
}
