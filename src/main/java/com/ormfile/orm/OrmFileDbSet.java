package com.ormfile.orm;

import com.ormfile.exeption.OrmFileDbSetWarning;
import com.ormfile.orm.annotations.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Clase para contexto para mapeo en base de datos
 * OrmFileDbSet es una clase que debe funcionar como contexto de las clases, esto quiere decir que para saber la
 * estructura en base de datos de una tabla, se debe de acceder a una instancia de la clase OrmFileDbSet que se
 * le ha pasado un objeto de clase como parámetro en el constructor.
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
public class OrmFileDbSet {
    /**
     * Objeto de clase para la estructura de la clase mapeada
     */
    private Class<?> class_structure;
    /**
     * Annotation OrmFile de la clase a mapear
     */
    private OrmFileEntity ormFileEntity;
    /**
     * Annotation OrmFile de la clase a mapear
     */
    private OrmFileTable ormFileTable;
    /**
     * Annotations OrmFile de la clase a mapear
     */
    private ArrayList<Field> columns = new ArrayList<>();

    /**
     * Método constructor de la clase
     *
     * @param classObj Objeto de clase para obtener la estructura de los Annotations de OrmFile
     */
    public OrmFileDbSet(Class<?> classObj) throws OrmFileDbSetWarning {
        this.class_structure = classObj;
        if (classObj != null) {
            setEntity();
            setTable();
            setColumns();
        } else {
            throw new NullPointerException("[ERROR] El objeto de clase no puede ser null");
        }
    }

    /**
     * Método para obtener el Annotation Entity
     *
     * @throws OrmFileDbSetWarning Alerta que en la clase no contiene el Annotation Entity
     */
    private void setEntity() throws OrmFileDbSetWarning {
        if (class_structure.isAnnotationPresent(OrmFileEntity.class)) {
            ormFileEntity = class_structure.getAnnotation(OrmFileEntity.class);
        } else {
            throw new OrmFileDbSetWarning("La clase no contiene configuración de entidad");
        }
    }

    /**
     * Método para obtener el Annotation Table
     *
     * @throws OrmFileDbSetWarning Alerta que la clase no contiene el Annotation Table
     */
    private void setTable() throws OrmFileDbSetWarning {
        if (class_structure.isAnnotationPresent(OrmFileTable.class)) {
            ormFileTable = class_structure.getAnnotation(OrmFileTable.class);
        } else {
            throw new OrmFileDbSetWarning("La clase no contiene configuración de tabla");
        }
    }

    /**
     * Método para obtener los Annotations Column
     *
     * @throws OrmFileDbSetWarning Alerta que la clase no contien el Annotation Column
     */
    private void setColumns() throws OrmFileDbSetWarning {
        for (Field field :
                class_structure.getDeclaredFields()) {
            if (field.isAnnotationPresent(OrmFileColumn.class)) {
                columns.add(field);
            }
        }
        if (columns.isEmpty()) {
            throw new OrmFileDbSetWarning("La clase no contiene Annotations de tipo @OrmFileColumn");
        }
    }

    /**
     * Método Get de la propiedad columns
     *
     * @return lista de propiedades mapeadas como columnas
     */
    public ArrayList<Field> getColumns() {
        return columns;
    }

    /**
     * Método Get de la propiedad class_structure
     *
     * @return objeto de clase de la clase mapeada
     */
    public Class<?> getClassStructure() {
        return class_structure;
    }

    /**
     * Método Get de la propiedad ormFileEntity
     *
     * @return Annotation de la clase OrmFileEntity
     * @see OrmFileEntity
     */
    public OrmFileEntity getOrmFileEntity() {
        return ormFileEntity;
    }

    /**
     * Método Get de la propiedad ormFileTable
     *
     * @return Annotation de tipo OrmFileTable
     * @see OrmFileTable
     */
    public OrmFileTable getOrmFileTable() {
        return ormFileTable;
    }
}
