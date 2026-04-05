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

import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.Payment;
import com.uade.tpo.demo.entity.Shipment;
import com.uade.tpo.demo.security.SecurityUtils;
import com.uade.tpo.demo.service.OrderService;
import com.uade.tpo.demo.service.PaymentService;
import com.uade.tpo.demo.service.ShipmentService;
import com.uade.tpo.demo.service.payload.EnvioCreateInput;
import com.uade.tpo.demo.service.payload.PagoCreateInput;
import com.uade.tpo.demo.service.payload.PedidoCreateInput;
import com.uade.tpo.demo.service.payload.PedidoUpdateInput;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> listarTodos() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@orderAccess.canAccess(authentication, #id)")
    public Order obtener(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order crear(@RequestBody(required = false) PedidoCreateInput input) {
        var user = SecurityUtils.currentUser();
        boolean admin = SecurityUtils.isAdmin(user);
        return orderService.createOrder(user.getId(), admin, input != null ? input : new PedidoCreateInput());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@orderAccess.canAccess(authentication, #id)")
    public Order actualizar(@PathVariable Long id, @RequestBody(required = false) PedidoUpdateInput input) {
        return orderService.updateOrder(id, input);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@orderAccess.canAccess(authentication, #id)")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/envios")
    @PreAuthorize("@orderAccess.canAccess(authentication, #id)")
    public ResponseEntity<Shipment> crearEnvio(@PathVariable Long id, @RequestBody EnvioCreateInput input) {
        Shipment shipment = shipmentService.createForOrder(id, input);
        return ResponseEntity.ok(shipment);
    }

    @PostMapping("/{id}/pagos")
    @PreAuthorize("@orderAccess.canAccess(authentication, #id)")
    public ResponseEntity<Payment> registrarPago(@PathVariable Long id, @RequestBody PagoCreateInput input) {
        Payment payment = paymentService.createForOrder(id, input);
        return ResponseEntity.ok(payment);
    }
}
