package com.ormfile.orm.annotations;

import com.ormfile.orm.enums.OrmFileDataTypesToString;

import java.lang.annotation.*;

/**
 * @author alexandevcwa
 * @version 1.0.0
 * Annotation para mapeo de columnas
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmFileColumn {
    /**
     * Annotation para nombre de la columna
     */
    String columnName() default "";

    /**
     * Annotation para mapear el tipo de datos que va a almacenar la columna
     */
    OrmFileDataTypesToString dataType();

    /**
     * Annotation para longitus de la propiedad
     */
    int length() default Integer.MAX_VALUE;

    /**
     * Annotation para permitir nulos o no permitir nulos en la columna
     */
    boolean isNullable() default false;

    /**
     * Annotation para volver una columna unique
     */
    boolean isUnique() default false;

    /**
     * Annotation para indexar una columna
     */
    boolean isIndexed() default false;

    /**
     * Annotation para setear un valor por default en una columna
     */
    String defaultValue() default "";
}
