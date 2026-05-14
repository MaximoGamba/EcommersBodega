package com.uade.tpo.demo.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        String uri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        boolean isReactivateUserEndpoint = "PUT".equalsIgnoreCase(request.getMethod())
                && uri != null
                && uri.matches(".*/usuarios/\\d+/activar$");
        boolean hasBearerToken = authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7);

        if (isReactivateUserEndpoint && hasBearerToken) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "No tenés permiso para acceder a este recurso");
            body.put("code", "FORBIDDEN");
            objectMapper.writeValue(response.getOutputStream(), body);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Credenciales ausentes o token inválido");
        body.put("code", "UNAUTHORIZED");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
