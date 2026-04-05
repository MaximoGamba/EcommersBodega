package com.uade.tpo.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Component("shipmentAccess")
@RequiredArgsConstructor
public class ShipmentAccess {

    private final ShipmentRepository shipmentRepository;

    /** Lectura o actualización: dueño del pedido o administrador. */
    public boolean canAccess(Authentication authentication, Long shipmentId) {
        if (authentication == null) {
            return false;
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }
        if (!(authentication.getPrincipal() instanceof User user)) {
            return false;
        }
        return shipmentRepository.findById(shipmentId)
                .map(s -> s.getOrder().getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}
