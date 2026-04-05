package com.uade.tpo.demo.controllers.pagos;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Payment;
import com.uade.tpo.demo.service.PaymentService;
import com.uade.tpo.demo.service.payload.PagoUpdateInput;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @PreAuthorize("@paymentAccess.canAccess(authentication, #id)")
    public Payment obtener(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@paymentAccess.canAccess(authentication, #id)")
    public Payment actualizar(@PathVariable Long id, @RequestBody(required = false) PagoUpdateInput input) {
        return paymentService.update(id, input);
    }
}
