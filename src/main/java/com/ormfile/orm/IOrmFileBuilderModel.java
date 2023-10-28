package com.ormfile.orm;

import java.util.ArrayList;

/**
 * Interfaz funcional para cargar todas las clases que van a ser mapeadas en base de datos por medio del
 * constructor super() oh por medio de un objeto de tipo IOrmFileBuilderModel sobrescribiendo sus métodos
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
@FunctionalInterface
public interface IOrmFileBuilderModel {
    /**
     * Método que retornar una lista de objetos de tipo OrmFileDbSet
     *
     * @return ArrayList de tipo OrmFileDbSet
     * @see OrmFileDbSet
     */
    ArrayList<OrmFileDbSet> mapping();
}
