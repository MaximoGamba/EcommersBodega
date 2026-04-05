package com.uade.tpo.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component("orderAccess")
@RequiredArgsConstructor
public class OrderAccess {

    private final OrderRepository orderRepository;

    public boolean canAccess(Authentication authentication, Long orderId) {
        if (authentication == null) {
            return false;
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }
        if (!(authentication.getPrincipal() instanceof User user)) {
            return false;
        }
        return orderRepository.findById(orderId)
                .map(o -> o.getUser().getId().equals(user.getId()))
                .orElse(false);
    }
}
