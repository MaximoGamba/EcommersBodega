package com.uade.tpo.demo.controllers.vinos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class VinoRequest {
    private String name;
    private String winery;
    private Integer year;
    private BigDecimal price;
    private Integer stock;
    private Long colorId;
    private Long cepaId;
    private Long azucarId;
    private Long crianzaId;
    private Long elaboracionId;
    private Long medidaId;
    private BigDecimal discountPercent;
}
