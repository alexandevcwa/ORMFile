package com.ormfile.orm;

import com.ormfile.exeption.OrmFileException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Interfaz para las operaciones de los archivos que servirán como persistencia de datos
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
public interface IOrmFileDatabase {

    /**
     * Crea toda la estructura de carpetas y archivos para almacenar los datos
     *
     * @throws IOException Se genéra cuando la escritura o lectura de un archivo o directoerio no se puede efectuar
     */
    void create() throws IOException;

    /**
     * Elimina todas las carpetas y archivos mapeados con las clases
     */
    void drop();
}