package com.uade.tpo.demo.controllers.usuarios;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.controllers.auth.RegisterRequest;
import com.uade.tpo.demo.entity.Order;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.security.SecurityUtils;
import com.uade.tpo.demo.service.OrderService;
import com.uade.tpo.demo.service.UserService;
import com.uade.tpo.demo.service.payload.UserUpdateInput;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listar() {
        return userService.getUsers().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}/pedidos")
    public List<Order> pedidosDelUsuario(@PathVariable Long id) {
        User current = SecurityUtils.currentUser();
        boolean isAdmin = SecurityUtils.isAdmin(current);
        if (!isAdmin && !current.getId().equals(id)) {
            throw new AccessDeniedException("No tenés permiso para acceder a este recurso");
        }
        // Fuerza 404 si el usuario no existe o está desactivado.
        userService.getUserById(id);
        return orderService.getOrdersByUserId(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return toResponse(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        UserUpdateInput input = new UserUpdateInput();
        BeanUtils.copyProperties(request, input);
        return toResponse(userService.updateUser(id, input));
    }

    @PutMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activar(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario activado correctamente"));
    }

    private UsuarioResponse toResponse(User user) {
        if (user.getRole() == null) {
            throw new BadRequestException("Usuario sin rol asignado");
        }
        return UsuarioResponse.builder()
                .id(user.getId())
            .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(user.getName())
                .role(user.getRole().getName())
                .build();
    }
}
