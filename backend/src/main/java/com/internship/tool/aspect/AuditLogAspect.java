// AuditLogAspect.java: Spring AOP aspect that intercepts @Auditable service methods.
// After successful method execution, saves an AuditLog entry with the current user from Spring Security context.

package com.internship.tool.aspect;

import com.internship.tool.annotation.Auditable;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    /**
     * Intercepts all methods annotated with @Auditable.
     * Extracts the entity ID from the first method argument if it is a UUID,
     * or from a DTO's getId() method as a fallback.
     */
    @AfterReturning(
        pointcut = "@annotation(com.internship.tool.annotation.Auditable)",
        returning = "returnValue"
    )
    public void logAudit(JoinPoint joinPoint, Object returnValue) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Auditable auditable = method.getAnnotation(Auditable.class);

            String entityType = auditable.entityType();
            String action = auditable.action();

            // Attempt to extract entity ID from method arguments
            UUID entityId = extractEntityId(joinPoint.getArgs(), returnValue);

            // Get current authenticated user
            String performedBy = getCurrentUser();

            AuditLog entry = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId != null ? entityId : UUID.randomUUID())
                    .action(action)
                    .performedBy(performedBy)
                    .performedAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(entry);
            log.info("Audit: {} {} {} by {}", action, entityType, entityId, performedBy);

        } catch (Exception e) {
            log.error("Failed to persist audit log entry: {}", e.getMessage(), e);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private UUID extractEntityId(Object[] args, Object returnValue) {
        // First, check if first argument is a UUID (e.g., softDelete(UUID id))
        if (args != null && args.length > 0 && args[0] instanceof UUID) {
            return (UUID) args[0];
        }
        // Second, check if return value exposes getId() returning UUID (e.g., create returns DTO)
        if (returnValue != null) {
            try {
                Method getId = returnValue.getClass().getMethod("getId");
                Object id = getId.invoke(returnValue);
                if (id instanceof UUID) return (UUID) id;
            } catch (Exception ignored) {
                // Not all return values will have UUID getId()
            }
        }
        return null;
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }
}
