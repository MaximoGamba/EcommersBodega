package com.uade.tpo.demo.controllers.pedidos;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.OrderItem;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pedidos/{pedidoId}/detalles")
@RequiredArgsConstructor
public class PedidoDetalleController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("@orderAccess.canAccess(authentication, #pedidoId)")
    public List<OrderItem> listar(@PathVariable Long pedidoId) {
        return orderService.getOrderItems(pedidoId);
    }

    @PostMapping
    @PreAuthorize("@orderAccess.canAccess(authentication, #pedidoId)")
    public ResponseEntity<OrderItem> agregar(@PathVariable Long pedidoId,
            @RequestBody DetallePedidoRequest request) {
        if (request.getWineId() == null || request.getQuantity() == null) {
            throw new BadRequestException("wineId y quantity son obligatorios");
        }
        OrderItem item = orderService.addOrderItem(pedidoId, request.getWineId(), request.getQuantity());
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{idDetalle}")
    @PreAuthorize("@orderAccess.canAccess(authentication, #pedidoId)")
    public OrderItem modificar(@PathVariable Long pedidoId, @PathVariable Long idDetalle,
            @RequestBody DetallePedidoUpdateRequest request) {
        if (request.getQuantity() == null) {
            throw new BadRequestException("quantity es obligatorio");
        }
        return orderService.updateOrderItem(pedidoId, idDetalle, request.getQuantity());
    }

    @DeleteMapping("/{idDetalle}")
    @PreAuthorize("@orderAccess.canAccess(authentication, #pedidoId)")
    public ResponseEntity<Void> quitar(@PathVariable Long pedidoId, @PathVariable Long idDetalle) {
        orderService.removeOrderItem(pedidoId, idDetalle);
        return ResponseEntity.noContent().build();
    }
}
