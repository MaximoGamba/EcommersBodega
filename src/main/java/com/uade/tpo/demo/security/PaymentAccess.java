package com.uade.tpo.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component("paymentAccess")
@RequiredArgsConstructor
public class PaymentAccess {

    private final PaymentRepository paymentRepository;

    /** Lectura o actualización: dueño del pedido o administrador. */
    public boolean canAccess(Authentication authentication, Long paymentId) {
        if (authentication == null) {
            return false;
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }
        if (!(authentication.getPrincipal() instanceof User user)) {
            return false;
        }
        return paymentRepository.findById(paymentId)
                .map(p -> p.getOrder().getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}
