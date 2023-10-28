package com.ormfile.orm;

import com.ormfile.orm.annotations.OrmFilePrimaryKey;
import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase para efectuar operaciones de obtención de datos por medio de consultas ORMFile a los archivos de persistencia
 *
 * @author alexandevcwa
 * @version 1.0.0
 * @see IOrmFileOperationsFilter
 */
public class OrmFileOperationsFilter implements IOrmFileOperationsFilter {

    /**
     * Columna a filtrar
     */
    private Field column;

    /**
     * Archivo que contiene los datos a filtrar contenido en el BufferedReader
     */
    BufferedReader reader;

    /**
     * Método constructor de clase para obtener datos por medio de la PK de la clase mapeada
     *
     * @param bufferedReader Buffer para lectura de registros del archivo que contiene los datos de la clase mapeada
     */
    public OrmFileOperationsFilter(BufferedReader bufferedReader) throws FileNotFoundException {
        this.reader = bufferedReader;
    }

    /**
     * Método constructor de clase para obtener datos de cualquier columna de la clase mapeada
     *
     * @param bufferedReader BufferedReader para obtener los datos del archivo que contiene los datos de la clase mapeada
     * @param field          columna a filtrar
     */
    public OrmFileOperationsFilter(BufferedReader bufferedReader, Field field) throws FileNotFoundException {
        this.reader = bufferedReader;
        this.column = field;
    }

    @Override
    public ArrayList<String[]> where(String value) throws IOException {

        ArrayList<String[]> recordsList = new ArrayList<>();

        String[] record = reader.readLine().split("\\|");
        int columnNumber;

        if (column == null) {
            columnNumber = getColumnIdByRegex(record, "PK_NAME");
        } else {
            columnNumber = getColumnIdByRegex(record, column.getName());
        }

        String line = null;
        while ((line = reader.readLine()) != null) {
            record = line.split("\\|");
            if (Objects.equals(record[columnNumber], value.toString().toUpperCase())) {
                recordsList.add(record);
            }
        }
        reader.close();
        return recordsList;
    }

    @Override
    public ArrayList<String[]> where(int value) throws IOException {
        ArrayList<String[]> recordsList = new ArrayList<>();

        String[] record = reader.readLine().split("\\|");

        int columnNumber;

        if (column == null) {
            columnNumber = getColumnIdByRegex(record, "PK_NAME");
        } else {
            columnNumber = getColumnIdByRegex(record, column.getName());
        }

        String line = null;
        while ((line = reader.readLine()) != null) {
            record = line.split("\\|");
            int numericValue = Integer.parseInt(record[columnNumber]);
            if (numericValue == value) {
                recordsList.add(record);
            }
        }
        reader.close();
        return recordsList;
    }

    @Override
    public ArrayList<String[]> where(UUID value) {
        throw new UnsupportedOperationException("Método sin implementación");
    }

    @Override
    public ArrayList<String[]> contains(String[] values) {
        throw new UnsupportedOperationException("Método sin implementación");
    }

    @Override
    public ArrayList<String[]> contains(int[] values) {
        throw new UnsupportedOperationException("Método sin implementación");
    }

    @Override
    public ArrayList<String[]> contains(UUID[] values) {
        throw new UnsupportedOperationException("Método sin implementación");
    }

    /**
     * Método que busca el número de columna que contiene el patrón definido, esta busqueda se efectua entre la propiedades de estructura de la columna
     *
     * @param columns_structure estructura de todas las columnas del archivo que actua como tabla
     * @param regex             patrón que debe de buscar entre todas las propiedades de la columna
     * @return Id donde se localiza la columna
     */
    public static int getColumnIdByRegex(@NotNull String[] columns_structure, String regex) {
        for (int i = 0; i < columns_structure.length; i++) {
            if (columns_structure[i].contains(regex)) {
                return i;
            }
        }
        return -1;
    }
}
