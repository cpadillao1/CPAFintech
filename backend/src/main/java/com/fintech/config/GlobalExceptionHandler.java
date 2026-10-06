package com.fintech.config;

import com.fintech.eod.exception.BusinessRuleException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  Private method para reutilizar la estructura del JSON
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status,
                                                                   String error,
                                                                   String message,
                                                                   String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }

    //  Errores de Credenciales (Login Fallido)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex,
                                                                    HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Incorrect username or password",
                request.getServletPath());
    }

    //  Errores de Validación (@Valid) - Unificamos la lista de errores en el campo 'message'
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                          HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                // Ordenamos para priorizar los errores de campos vacíos/obligatorios al inicio
                .sorted(Comparator.comparing(error -> {
                    String code = error.getCode();
                    // Si es NotBlank, NotNull o NotEmpty, les damos prioridad (valor 0)
                    if ("NotBlank".equals(code) || "NotNull".equals(code) || "NotEmpty".equals(code)) {
                        return 0;
                    }
                    // Cualquier otro error va después (valor 1)
                    return 1;
                }))
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Validation Error",
                details,
                request.getServletPath());
    }

    //  Acceso Denegado (403 Forbidden - Falta de Roles)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex,
                                                                  HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "Forbidden",
                "No tienes permisos suficientes para realizar esta acción.",
                request.getServletPath());
    }

    //  Manejo de Recursos No Encontrados (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex,
                                                                    HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getServletPath());
    }

    //  Duplicados en Base de Datos (Integridad)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {

        Throwable rootCause = ex.getMostSpecificCause();
        String dbMessage = rootCause.getMessage();

        String friendlyMessage = "Database integrity error.";

        // 1. Detectar campos NULL que son obligatorios (Tu error actual)
        if (dbMessage.contains("null value in column")) {
            // Extraemos el nombre de la columna del mensaje de Postgres
            String column = extractColumnFromMessage(dbMessage);
            friendlyMessage = "The field '" + column + "' It is mandatory, and it arrived empty.";
        }

        // 2. Detectar Llaves Foráneas (Catálogos que no existen)
        else if (dbMessage.contains("is not present in table") || dbMessage.contains("violates foreign key")) {
            friendlyMessage = "One of the catalog IDs does not exist in the system.";
        }

        // 3. Detectar Duplicados (DNI, Email)
        else if (dbMessage.contains("already exists") || dbMessage.contains("duplicate key")) {
            friendlyMessage = "The record already exists).";
        }

        // Log para el desarrollador en consola
        System.err.println("DB ERROR DETAIL: " + dbMessage);

        //return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", message, request.getServletPath());
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", friendlyMessage, request.getServletPath());
    }

    //  NUEVO: Atrapa CUALQUIER regla de negocio fallida de toda tu aplicación
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        // Extrae dinámicamente el HttpStatus que enviaste desde el Service
        return buildErrorResponse(ex.getHttpStatus(), "Business Rule Violation", ex.getMessage(), request.getServletPath());
    }

    //  El "Paracaídas": Cualquier error no controlado (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getServletPath());
    }

    // Metodo auxiliar para limpiar el nombre de la columna
    private String extractColumnFromMessage(String message) {
        try {
            // Postgres suele enviar: null value in column "created_by" ...
            int start = message.indexOf("\"") + 1;
            int end = message.indexOf("\"", start);
            return message.substring(start, end);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
