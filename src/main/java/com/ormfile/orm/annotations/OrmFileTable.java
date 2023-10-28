package com.ormfile.orm.annotations;

import java.lang.annotation.*;

/**
 * @author alexandevcwa
 * @version 1.0.0
 * Annotation para mapeo de tablas
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmFileTable {
    /**
     * Nombre de la tabla
     */
    String tableName() default "";

    /**
     * Schema de tabla
     */
    String tableSchema() default "";
}
