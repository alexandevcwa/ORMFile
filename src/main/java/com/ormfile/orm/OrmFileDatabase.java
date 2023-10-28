package com.ormfile.orm;

import com.ormfile.io.OrmFIleIO;
import com.ormfile.orm.annotations.OrmFileColumn;
import com.ormfile.orm.annotations.OrmFileForeignKey;
import com.ormfile.orm.annotations.OrmFilePrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que cumple la tarea de crear y eliminar los archivos de las clases mapeadas
 *
 * @author alexandevcwa
 * @version 1.0.0
 * @see IOrmFileDatabase
 */
public class OrmFileDatabase implements IOrmFileDatabase {

    /**
     * Lista de tablas a mapear como archivos persistentes
     */
    private final ArrayList<OrmFileDbSet> ormFileDbSets;

    /**
     * Directorio general donde se almacenan lo archivos de persistencia de datos
     */
    private final File generalDirectory;

    /**
     * Lista de llaves primarias que trabajan con secuencia
     */
    private final ArrayList<String> primaryKeysSequences = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(OrmFileDatabase.class.getName());

    /**
     * Método constructor de clase
     *
     * @param ormFileDbSets ArrayList con los objetos de clase de las clases mapeadas
     */
    public OrmFileDatabase(@NotNull ArrayList<OrmFileDbSet> ormFileDbSets) {
        this.ormFileDbSets = ormFileDbSets;
        this.generalDirectory = new File(System.getProperty("user.dir"));
    }

    /**
     * Método constructor de clase
     *
     * @param ormFileDbSets    ArrayList con los objetos de clase de las clases mapeadas
     * @param generalDirectory objeto con el directorio general de los archivos de persistencia de datos
     */
    public OrmFileDatabase(@NotNull ArrayList<OrmFileDbSet> ormFileDbSets, File generalDirectory) {
        this.ormFileDbSets = ormFileDbSets;
        this.generalDirectory = new File(generalDirectory.getAbsolutePath());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create() throws IOException {
        createDatabaseStructure();
        createPKSequences();
    }

    /**
     * Crear toda la estructura de carpetas, archivos y la línea de estructura que va en cada archivo en su primera
     * línea del formato de la tabla
     *
     * @throws IOException Se lanza por falta de permisos en el sistema operativo
     */
    private void createDatabaseStructure() throws IOException {
        for (OrmFileDbSet dbSet :
                ormFileDbSets) {

            String schema_name = generalDirectory.getPath() + "\\" + (dbSet.getOrmFileEntity().schema().isEmpty() ? "general" : dbSet.getOrmFileEntity().schema());

            File schema = new File(schema_name);

            if (!schema.exists()) {
                //Crear carpeta que funciona como schema de tablas
                if (schema.mkdirs()) {
                    logger.info("El directorio [" + schema_name + "] se creo correctamente");
                } else {
                    throw new IOException("No se pudo crear el directorio en: " + schema.getPath());
                }
            } else {
                logger.info("El directorio [" + schema.getName() + "] ya existe");
            }
            //Crear archivo de tabla asociada al schema creado
            createFilesAsTables(schema, dbSet);
            schema = null;
            schema_name = null;
        }
    }

    /**
     * Crea totods los archivos de las clases mapeadas
     *
     * @param schema Directorio donde se va a almacenar el archivo de la clase mapeada
     * @param dbSet  Objeto que contine la estructura de la clase mapeada para crear el archivo
     * @see OrmFileDbSet
     * @see File
     */
    private void createFilesAsTables(File schema, OrmFileDbSet dbSet) throws IOException {

        String table_name = dbSet.getOrmFileTable().tableName().isEmpty() ?
                dbSet.getClassStructure().getSimpleName() : dbSet.getOrmFileTable().tableName();

        File file_table = new File(schema.getPath() + "\\" + table_name + ".dbf");

        if (!file_table.exists()) {
            if (file_table.createNewFile()) {
                logger.info("Archivo [" + file_table + "] se ha creado correctamente");
                createStructureLine(file_table, dbSet);
            } else {
                throw new IOException("No se pudo crear el archivo de tabla " + file_table.getName());
            }
        } else {
            logger.info("El archivo [" + table_name + "] ya existe en el directorio de schema " + schema.getPath());
        }

        table_name = null;
        file_table = null;
    }

    /**
     * Crea el formato de la clase mapeada para conocer el orden de las columnas, nombre, PK y FK
     *
     * @param fileTable Objeto para obtener el archivo de la clase mapeada
     * @param dbSet     Objeto que contine la estructura de la clase mapeada para crear el archivo
     * @see OrmFileDbSet
     * @see File
     */
    private void createStructureLine(@NotNull File fileTable, @NotNull OrmFileDbSet dbSet) throws IOException {
        String columns_structure = "";

        //Formato de la linea que contiene la estructura de la tabla
        for (Field table_column :
                dbSet.getColumns()) {
            var pkAnnotation = table_column.getDeclaredAnnotation(OrmFilePrimaryKey.class);                 //Obtener el Annotation de Llave Primaria
            var fkAnnotation = table_column.getDeclaredAnnotation(OrmFileForeignKey.class);                 //Obtener el Annotation de Llave Foranea
            columns_structure += generateFieldLineFormat(pkAnnotation, fkAnnotation, table_column, dbSet.getClassStructure()) + "|";
        }
        //Guardar Linea de estructura de tabla
        OrmFIleIO.WriteLine(fileTable, columns_structure);
        fileTable = null;
        columns_structure = null;
    }

    /**
     * Genéra la línea de estructura del orden de las columnas en los archivos de datos persistentes
     *
     * @param pk     Annotation para identificar la llave primaria en una propiedad de una clase mapeada
     * @param fk     Annotation para identificar la llave foranea en una propiedad de una clase mapeada
     * @param column Propiedad de una clase mapeada
     * @return String con la estuctura que tiene la propiedad que va a actuar como columna en un archivo de persistencia de datos
     * @see OrmFilePrimaryKey
     * @see OrmFileForeignKey
     * @see Field
     */
    private String generateFieldLineFormat(OrmFilePrimaryKey pk, OrmFileForeignKey fk, @NotNull Field column, Class<?> mapper_class) {

        String column_structure = null;

        column_structure = "COLUMN_NAME=" + (column.getDeclaredAnnotation(OrmFileColumn.class).columnName().isEmpty() ?
                (column.getName()) : column.getDeclaredAnnotation(OrmFileColumn.class).columnName()) + ":";

        column_structure += "DATA_TYPE=" + column.getDeclaredAnnotation(OrmFileColumn.class).dataType() + ":";

        column_structure += "UNIQUE=" + column.getDeclaredAnnotation(OrmFileColumn.class).isUnique();

        if (pk != null) {
            column_structure += ":PK_NAME=" + (pk.pkName().isEmpty() ? column.getName() + "_PK" : pk.pkName());
            //Add to primary keys list
            if (pk.isAutoIncrement()) {
                String newLine = mapper_class.getName()                                                 //Classname that have the PK property
                        + "|" + (pk.pkName().isEmpty() ? column.getName() + "_PK" : pk.pkName())        //PK name
                        + "|" + (-1);                                                                   //Sequence initial value
                primaryKeysSequences.add(newLine);
            }
        }
        if (fk != null) {
            column_structure += ":FK_NAME=" + (fk.fkName().isEmpty() ? column.getName() + "_FK" : fk.fkName());
        }
        return column_structure;
    }

    /**
     * Crear el archivo de sistema que contiene las llaves primarias definidas con autoincremento
     *
     * @throws IOException si el archivo no se puede crear es lanzada
     */
    private void createPKSequences() throws IOException {
        File sys_sequence = new File(generalDirectory.getAbsolutePath() + "\\sys");
        if (!sys_sequence.exists()) {
            if (sys_sequence.mkdirs()) {
                logger.info("Directorio de systema [" + sys_sequence.getName() + "] creado correctamente");
            } else {
                throw new IOException("El directorio " + sys_sequence.getName() + " no se pudo crear");
            }
        }

        sys_sequence = new File(sys_sequence.getAbsoluteFile() + "\\sys_pk_sequences.dbf");

        if (!sys_sequence.exists() && !primaryKeysSequences.isEmpty()) {
            if (sys_sequence.createNewFile()) {
                logger.info("[SEQUENCE] " + sys_sequence.getName() + " creado correctamente");
                OrmFIleIO.WriteLines(sys_sequence, primaryKeysSequences);
            } else {
                throw new IOException("No se pudo crear el archivo " + sys_sequence.getName());
            }
        } else {
            logger.warning("El archivo " + sys_sequence.getName() + " ya existe");
        }
    }

    /**
     * {@docRoot}
     */
    @Override
    public void drop() {
        File[] schemas = generalDirectory.listFiles();

        if (schemas.length == 0) {
            logger.log(Level.WARNING, "No existen directorios de schemas o archivos de tablas para hacer un drop de datos");
            schemas = null;
            return;
        }

        for (File schema :
                schemas) {
            if (!schema.isDirectory()) {
                break;
            }

            File[] schema_tables = schema.listFiles();
            for (File table :
                    schema_tables) {
                if (!table.isFile()) {
                    break;
                }
                if (table.delete()) {
                    logger.info("[DROP TABLE] Archivo (" + table.getName() + ") ha sido eliminada");
                }
            }

            if (schema.delete()) {
                logger.info("[DROP SCHEMA] Directorio (" + schema.getName() + ") ha sido eliminado");
            }
        }
    }
}
