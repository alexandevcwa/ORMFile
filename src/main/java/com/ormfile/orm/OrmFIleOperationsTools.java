package com.ormfile.orm;

import com.ormfile.orm.annotations.OrmFileEntity;
import com.ormfile.orm.annotations.OrmFileTable;

import java.io.*;
import java.util.ArrayList;

/**
 * Clase para métodos que sirven para manejar la lectura y escritura de archivos
 */
public class OrmFIleOperationsTools {

    /**
     * Lectura de archivos de persistencia de datos que almacenan los datos de las clases mapeadas
     *
     * @param globalDirectory Directorio global de los schemas y tablas de persistencia de datos
     * @param mapped_class    Clase mapeada a la cuál se van a obtener los datos
     * @return BufferedReader configurado para leerlo y obtener los datos
     * @throws FileNotFoundException Se lanza cuando no se encuentra el archivo de persistencia de datos
     */
    static BufferedReader readFileTable(File globalDirectory, Class<?> mapped_class) throws FileNotFoundException {
        String schema = mapped_class.getDeclaredAnnotation(OrmFileEntity.class).schema().isEmpty() ?
                "general" : mapped_class.getDeclaredAnnotation(OrmFileEntity.class).schema();
        String table = mapped_class.getDeclaredAnnotation(OrmFileTable.class).tableName().isEmpty() ?
                mapped_class.getSimpleName() + ".dbf" : mapped_class.getDeclaredAnnotation(OrmFileTable.class).tableName() + ".dbf";
        FileReader reader = new FileReader(globalDirectory.getAbsolutePath() + "\\" + schema + "\\" + table);
        return new BufferedReader(reader);
    }

    /**
     * Lectura de archivos de persistencia de datos que no son clases mapeadas
     *
     * @param globalDirecroty Direcotio global de los schemas y tablas de persistencia de datos
     * @param schema          Directorio que actua como schema para almacenar los archivos de persistencia de datos
     * @param table           Archivo de persistencia de datos
     * @return BufferedReader configurado para leerlo y obtener los datos
     * @throws FileNotFoundException Se lanza cuando no es encuentra el archivo de persistencia de datos
     */
    static BufferedReader readFileTable(File globalDirecroty, String schema, String table) throws FileNotFoundException {
        FileReader reader = new FileReader(globalDirecroty + "\\" + schema + "\\" + table + ".dbf");
        return new BufferedReader(reader);
    }

    /**
     * Guarda un registro en los archivos de persistencia de datos
     *
     * @param globalDirectory Directorio global de los schemas y tablas de persistencia de datos
     * @param schema          Directorio que actua como schema para almacenar los archivos de persistencia de datos
     * @param table           Archivo de persistencia de datos
     * @param lst             Lista que contiene los registros para guardar en los archivos de persistencia de datos
     * @return True si los archivos fueron actualizados correctamente
     */
    static boolean saveFileTable(File globalDirectory, String schema, String table, ArrayList<String> lst) throws IOException {
        String pathTable = globalDirectory.getAbsolutePath() + "\\" + schema + "\\" + table + ".dbf";

        BufferedReader reader = new BufferedReader(new FileReader(pathTable));

        StringBuilder builder = new StringBuilder();

        String line = null;

        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        for (String newLine : lst) {
            builder.append(newLine).append("\n");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(pathTable));
        writer.write(builder.toString());

        reader.close();
        //builder = null;
        writer.close();
        return true;
    }

    /**
     * Actualiza los registros de los archivos de persistencdia de datos
     *
     * @param schema Directorio que actua como schema para almacenar los archivos de persistencia de datos
     * @param table  Archivo de persistencia de datos
     * @param lst    Lista que contiene los registros para actualizar en los archivos de persistencia de datos
     * @return True si los archivos fueron actualizados correctamente
     * @throws IOException Se lanza cuando no se encuentra el archivo de persistencia de datos
     */
    static boolean updateFileTable(File globalDirectory, String schema, String table, ArrayList<String> lst) throws IOException {
        String pathTable = globalDirectory.getAbsolutePath() + "\\" + schema + "\\" + table + ".dbf";

        StringBuilder builder = new StringBuilder();

        for (String newLine : lst) {
            builder.append(newLine).append("\n");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(pathTable));
        writer.write(builder.toString());
        writer.close();
        //builder = null;
        return true;
    }
}
