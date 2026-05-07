// Auditable.java: Custom annotation to mark service methods that should generate audit log entries.
// Used by AuditLogAspect via @AfterReturning to capture entity changes.

package com.internship.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The logical name of the entity being audited (e.g., "BoardMinutes").
     */
    String entityType();

    /**
     * The action being performed: CREATE, UPDATE, or DELETE.
     */
    String action();
}
