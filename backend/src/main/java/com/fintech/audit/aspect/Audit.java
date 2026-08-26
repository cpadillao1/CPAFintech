package com.fintech.audit.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Se usa sobre métodos
@Retention(RetentionPolicy.RUNTIME) // Disponible en tiempo de ejecución
public @interface Audit {
    String action(); // Ejemplo: "CREATE"
    String module(); // Ejemplo: "BRANCHES"
}
