package com.uade.tpo.demo.service.payload;

import java.util.List;

import lombok.Data;

@Data
public class PedidoCreateInput {
    /** Si es null, el pedido queda asociado al usuario autenticado. */
    private Long userId;
    private List<OrderLineInput> items;
}
