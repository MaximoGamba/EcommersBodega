package com.uade.tpo.demo.service.payload;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PagoCreateInput {
    private BigDecimal amount;
    private String method;
}
