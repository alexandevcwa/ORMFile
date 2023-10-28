package com.ormfile.orm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Interfaz para los métodos de busqueda de los registros
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
public interface IOrmFileOperations {

    /**
     * Obtiene los registros buscando automaticamente con la llave primaria
     *
     * @return retorna un objeto de tipo OrmOperationsFilter con los métodos permitidos por la interfaz IOrmOperationsFilter
     */
    IOrmFileOperationsFilter getRecordByPk() throws FileNotFoundException;


    /**
     * Obtiene los registros buscando en una columna especificada
     *
     * @param column Columna mapeada de donde se van a filtrar los datos
     * @return retorna un objeto de tipo OrmOperationsFilter con los métodos permitidos por la interfaz IOrmOperationsFilter
     */
    IOrmFileOperationsFilter getRecordByColumn(Field column) throws FileNotFoundException;

    /**
     * Obtener todos los registros de la clase mapeada consultada
     *
     * @return Lista de arrays de tipo String
     */
    ArrayList<String[]> getAllRecords() throws IOException;


}
