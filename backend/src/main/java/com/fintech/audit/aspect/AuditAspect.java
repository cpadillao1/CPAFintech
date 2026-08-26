package com.fintech.audit.aspect;

import com.fintech.audit.domain.AuditLogEntity;
import com.fintech.audit.service.AuditLogService;
import com.fintech.management.users.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final HttpServletRequest request;

    @Around("@annotation(auditAnnotation)")
    public Object auditProcess(ProceedingJoinPoint joinPoint, Audit auditAnnotation) throws Throwable {
        String username = extractUsername(joinPoint, auditAnnotation);
        Object result;

        try {
            // 1. Intentamos ejecutar el metodo
            result = joinPoint.proceed();

            // 2. Si llegamos aqui sin excepcion, guardamos SUCCESS
            saveLog(username, auditAnnotation, "Processing: " + result.toString(), "SUCCESS");
            return result;

        } catch (Throwable ex) {
            // 3. Si algo falla (Duplicado, validacion, etc.), guardamos ERROR
            saveLog(username, auditAnnotation, "Error: " + ex.getMessage(), "ERROR");
            throw ex; // Re-lanzamos para que el GlobalExceptionHandler haga su parte
        }
    }

    private String extractUsername(JoinPoint joinPoint, Audit audit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username) && "AUTH".equals(audit.module())) {
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof LoginRequest loginReq) return loginReq.getLogin();
            }
        }
        return username;
    }

    private void saveLog(String username, Audit audit, String detail, String status) {
        AuditLogEntity logEntry = AuditLogEntity.builder()
                .username(username)
                .action(audit.action())
                .module(audit.module())
                .status(status)
                .detail(detail)
                .ipAddress(request.getRemoteAddr())
                .auditDate(LocalDate.now())
                .timestamp(LocalDateTime.now())
                .build();

        auditLogService.saveIndependentLog(logEntry);
    }
}
