package com.uade.tpo.demo.service.payload;

import lombok.Data;

@Data
public class OrderLineInput {
    private Long wineId;
    private Integer quantity;
}
