package com.uade.tpo.demo.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
        log.debug("Recurso no encontrado: {}", ex.getMessage());
        return body(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> badRequest(BadRequestException ex) {
        log.debug("Bad request: {}", ex.getMessage());
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST");
    }

    @ExceptionHandler(CategoryDuplicateException.class)
    public ResponseEntity<Map<String, Object>> duplicateCategory(CategoryDuplicateException ex) {
        log.debug("Categoría duplicada");
        return body(HttpStatus.CONFLICT, "Ya existe una categoría con esa descripción", "DUPLICATE_CATEGORY");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validationFailed(MethodArgumentNotValidException ex) {
        log.debug("Validación @Valid fallida: {}", ex.getMessage());
        Map<String, String> violations = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "inválido",
                        (a, b) -> a + "; " + b,
                        LinkedHashMap::new));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Datos de entrada inválidos");
        body.put("code", "VALIDATION_ERROR");
        body.put("violations", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> constraintViolation(ConstraintViolationException ex) {
        log.debug("Constraint violation: {}", ex.getMessage());
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (a, b) -> a + "; " + b,
                        LinkedHashMap::new));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Restricción de validación incumplida");
        body.put("code", "CONSTRAINT_VIOLATION");
        body.put("violations", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> notReadable(HttpMessageNotReadableException ex) {
        log.debug("JSON no legible", ex);
        String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return body(HttpStatus.BAD_REQUEST,
                "Cuerpo de la petición JSON inválido o mal formado" + (detail != null ? ": " + detail : ""),
                "INVALID_JSON");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> missingParam(MissingServletRequestParameterException ex) {
        log.debug("Parámetro faltante: {}", ex.getParameterName());
        return body(HttpStatus.BAD_REQUEST,
                "Falta el parámetro requerido: " + ex.getParameterName(),
                "MISSING_PARAMETER");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeMismatch(MethodArgumentTypeMismatchException ex) {
        log.debug("Tipo de argumento incorrecto: {}", ex.getName());
        return body(HttpStatus.BAD_REQUEST,
                "Valor inválido para '" + ex.getName() + "': se esperaba otro tipo",
                "TYPE_MISMATCH");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDenied(AccessDeniedException ex) {
        log.debug("Acceso denegado (capa MVC): {}", ex.getMessage());
        return body(HttpStatus.FORBIDDEN, "No tenés permiso para acceder a este recurso", "FORBIDDEN");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> badCredentials(BadCredentialsException ex) {
        log.debug("Credenciales inválidas");
        return body(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", "UNAUTHORIZED");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> authentication(AuthenticationException ex) {
        log.debug("Fallo de autenticación: {}", ex.getMessage());
        return body(HttpStatus.UNAUTHORIZED,
                ex.getMessage() != null ? ex.getMessage() : "Autenticación requerida o inválida",
                "UNAUTHORIZED");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> dataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        return body(HttpStatus.CONFLICT,
                "No se pudo completar la operación por una restricción de base de datos (por ejemplo, valor duplicado)",
                "DATA_INTEGRITY");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgument(IllegalArgumentException ex) {
        log.debug("IllegalArgumentException: {}", ex.getMessage());
        return body(HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "Argumento inválido",
                "ILLEGAL_ARGUMENT");
    }

    private static ResponseEntity<Map<String, Object>> body(HttpStatus status, String message, String code) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        body.put("code", code);
        return ResponseEntity.status(status).body(body);
    }
}
