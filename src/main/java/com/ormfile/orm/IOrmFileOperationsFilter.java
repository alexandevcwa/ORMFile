package com.ormfile.orm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Interfaz con metodos de filtración de datos
 */
public interface IOrmFileOperationsFilter {
    /**
     * Filtrar por cadena
     */
    ArrayList<String[]> where(String value) throws IOException;

    /**
     * Filtrar por valor numerico entero
     */
    ArrayList<String[]> where(int value) throws IOException;

    /**
     * Filtrar por UUID
     */
    ArrayList<String[]> where(UUID value);

    /**
     * Filtrar por valior valores de tipo cadena
     */
    ArrayList<String[]> contains(String[] values);

    /**
     * Filtrar por varios valores de tipo numerico entero
     */
    ArrayList<String[]> contains(int[] values);

    /**
     * Filtrar por varios valores de tipo UUID
     */
    ArrayList<String[]> contains(UUID[] values);
}
