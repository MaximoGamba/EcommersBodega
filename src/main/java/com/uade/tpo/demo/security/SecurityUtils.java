package com.uade.tpo.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.BadRequestException;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new BadRequestException("Usuario no autenticado");
        }
        return user;
    }

    public static boolean isAdmin(User user) {
        return user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
