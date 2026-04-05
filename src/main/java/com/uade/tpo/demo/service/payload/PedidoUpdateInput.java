package com.uade.tpo.demo.service.payload;

import java.math.BigDecimal;

import com.uade.tpo.demo.entity.OrderStatus;

import lombok.Data;

@Data
public class PedidoUpdateInput {
    private OrderStatus status;
    private BigDecimal total;
}
