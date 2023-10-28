package com.ormfile.orm;

import com.ormfile.exeption.OrmFileDbSetException;

import java.io.FileNotFoundException;

/**
 * Interfaz para obtener las operaciones que se pueden efectuar solbre la base de datos de archivos persistentes
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
public interface IOrmFileDbContext {

    /**
     * Obtiene las operaciones que se pueden efectuar sobre los datos de los archivos de las clases mapeadas
     *
     * @param obj Objeto de clase mapeada de la que se requiere hacer una operación en sus datos
     * @return objeto OrmFileOperations con las operaciones permitidas por la interfaz IOrmFileOperations
     * @throws OrmFileDbSetException Se lanza cuando se trata de mapear una clase que no pertenece al contexto
     * @see IOrmFileOperations
     */
    IOrmFileOperations select(Class<?> obj) throws OrmFileDbSetException, FileNotFoundException;

    /**
     * Obtiene las operaciones que se pueden efectuar sobre los directorios y archivos que contienen la persistencia de los datos
     *
     * @return Objeto de tipo OrmFileDatabase con los métodos autorizados de la interfaz IOrmFileDatabase para las operaciones sobre los directorios y archivos de datos persistentes
     */
    IOrmFileDatabase database();

    /**
     * Método para la insersión de datos en los arhivos de base de datos
     *
     * @param record objeto con los datos a guardar en base de datos
     * @return Objeto de tipo IOrmFileRecord que contiene los metodos autorizados
     * @see IOrmFileOperations
     * @see OrmFileRecord
     */
    IOrmFileRecord insert(OrmFileRecord record);

}
