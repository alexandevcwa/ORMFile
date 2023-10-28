package com.ormfile.orm.annotations;

import java.lang.annotation.*;

/**
 * @author alexandevcwa
 * @version 1.0.0
 * Annotation para configurar una clase como entidad
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmFileEntity {
    /**
     * Nombre del archivo de texto plano donde se guardaran los datos de esta entidad mapeada
     */
    String value() default "";

    /**
     * Nombre del esquema
     */
    String schema() default "";
}
