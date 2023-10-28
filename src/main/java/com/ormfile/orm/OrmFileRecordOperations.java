package com.ormfile.orm;

import com.ormfile.orm.annotations.OrmFileEntity;
import com.ormfile.orm.annotations.OrmFileTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class OrmFileRecordOperations {


    /**
     * Directorio global de persistencia de datos donde se encuentran los schemas y archivos de tablas
     */
    private static File globalDirectory;

    public static File getGlobalDirectory() {
        return globalDirectory;
    }

    public static void setGlobalDirectory(File globalDirectory) {
        OrmFileRecordOperations.globalDirectory = globalDirectory;
    }

    /**
     * Obtener el número de secuencia siguiente de una columna que esta mapeada como autoincremental
     *
     * @param class_fullname Nombre completo de la calse
     * @param pkName         Nombre de la llace primaria
     * @return El valor siguiente de la secuencia
     * @throws IOException Se lanza cuando no se encuentra el archivo de base de datos
     */
    protected static int getNewPkSequenceNumber(String class_fullname, String pkName) throws IOException {

        BufferedReader buffer = OrmFIleOperationsTools.readFileTable(globalDirectory, "sys", "sys_pk_sequences");

        ArrayList<String> aux = new ArrayList<>();

        String line = null;

        int sequenceValue = -1;

        while ((line = buffer.readLine()) != null) {
            String[] sequence = line.split("\\|");
            if (sequence[0].equals(class_fullname) && sequence[1].equals(pkName)) {
                sequenceValue = Integer.parseInt(sequence[2]) + 1;
                line = sequence[0] + "|" + sequence[1] + "|" + sequenceValue;
            }
            aux.add(line);
        }
        OrmFIleOperationsTools.updateFileTable(globalDirectory, "sys", "sys_pk_sequences", aux);
        return sequenceValue;
    }

    /**
     * Verifica unicidad del valor que se insertara en una columna con un constraint de tipo PK o UK existe
     *
     * @param mapper_class clase mapeada
     * @param column       columna a verificar
     * @param value        valor a verificar
     * @return True si el valor aún no existe, False  si el valor ya existe
     * @throws IOException Si el archivo no existe donde se almacenan los datos
     */
    protected static boolean verifyUniqueness(Class<?> mapper_class, Field column, String value) throws IOException {
        String schema = mapper_class.getDeclaredAnnotation(OrmFileEntity.class).schema();
        schema = schema.isEmpty() ? "general" : schema;

        String tableName = mapper_class.getDeclaredAnnotation(OrmFileTable.class).tableName();
        tableName = tableName.isEmpty() ? mapper_class.getSimpleName() : tableName;

        BufferedReader buffer = OrmFIleOperationsTools.readFileTable(globalDirectory, schema, tableName);

        String line = buffer.readLine();

        int id = OrmFileOperationsFilter.getColumnIdByRegex(line.split("\\|"), column.getName());

        boolean verification = false;

        //Analiza si el registro ya existe
        while ((line = buffer.readLine()) != null) {
            String[] record = line.split("\\|");
            if (record[id].toUpperCase().equals(value.toUpperCase())) {
                return false;
            }
        }
        //El registro no existe
        return true;
    }
}