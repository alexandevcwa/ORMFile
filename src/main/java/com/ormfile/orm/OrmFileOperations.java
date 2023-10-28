package com.ormfile.orm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Clase que contiene las operaciones que se pueden efectuar en la base de datos para obtener, agragar y modificar datos
 *
 * @author alexandevcwa
 * @version 1.0.0
 * @see IOrmFileOperations
 */
public class OrmFileOperations implements IOrmFileOperations {

    /**
     * Buffer que contiene el archivo con los datos de la clase mapeada
     */
    private final BufferedReader bufferedReader;

    /**
     * Directory general donde se encuentran almacenados los schemas y archivos de tablas,
     * este directorio no cambia ya que en el se almacenan todos los datos
     */
    private static File globalDirectory;


    /**
     * Método constructor de la clase
     *
     * @param mapped_class    clase mapeada
     * @param globalDirectory directorio global donde se encuentras los archivos de persistencia de datos
     */
    public OrmFileOperations(Class<?> mapped_class, File globalDirectory) throws FileNotFoundException {
        this.globalDirectory = globalDirectory;
        this.bufferedReader = OrmFIleOperationsTools.readFileTable(globalDirectory, mapped_class);
    }

    @Override
    public IOrmFileOperationsFilter getRecordByPk() throws FileNotFoundException {
        return new OrmFileOperationsFilter(this.bufferedReader);
    }

    @Override
    public IOrmFileOperationsFilter getRecordByColumn(Field column) throws FileNotFoundException {

        return new OrmFileOperationsFilter(this.bufferedReader, column);
    }

    @Override
    public ArrayList<String[]> getAllRecords() throws IOException {
        ArrayList<String[]> recordsList = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            recordsList.add(line.split("\\|"));
        }
        return recordsList;
    }
}
