package com.uade.tpo.demo.controllers.vinos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class VinoDescuentoRequest {
    private BigDecimal discountPercent;
}
