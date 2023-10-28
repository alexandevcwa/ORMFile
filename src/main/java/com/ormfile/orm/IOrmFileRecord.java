package com.ormfile.orm;

import java.io.IOException;

/**
 * Interfaz para efectuar operaciones de guardado de datos en base de datos de archivos
 */
public interface IOrmFileRecord<T> {
    /**
     * Método para guardar los datos procesados
     *
     * @param mapped_class clase mapeada de la cual se van a guardar los datos
     * @return True si los datos se guardaron correctamente
     * @throws IOException            si el arhivo de base de datos no es encontrado
     * @throws IllegalAccessException si no se puede acceder a las propiedades de los objetos de las clases mapeadas
     */
    boolean save(Class<T> mapped_class) throws IllegalAccessException, IOException;
}
