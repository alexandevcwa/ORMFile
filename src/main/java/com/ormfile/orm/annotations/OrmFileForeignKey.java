package com.ormfile.orm.annotations;

import java.lang.annotation.*;

/**
 * @author alexandevcwa
 * @version 1.0.0
 * Annotation para mapeo de llave fotanea
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmFileForeignKey {
    /**
     * Nombre que identifica la llave primaria
     */
    String fkName() default "";
}
