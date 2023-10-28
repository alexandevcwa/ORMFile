package com.ormfile.orm.annotations;

import java.lang.annotation.*;

/**
 * @author alexandevcwa
 * @version 1.0.0
 * Annotation para mapeo de llaves primarias
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmFilePrimaryKey {
    /**
     * Nombre para identificar la llave primaria
     */
    String pkName() default "";

    boolean isAutoIncrement() default false;
}
