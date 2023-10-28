package com.ormfile.orm;

import com.ormfile.exeption.OrmFileDbSetException;
import com.ormfile.exeption.OrmFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Clase que almacena todas las clases mapeadas como objetos de archivos secuenciales
 *
 * @author alexandevcwa
 * @version 1.0.0
 */
public class OrmFileDbContext implements IOrmFileDbContext {

    private static Logger logger = Logger.getLogger(OrmFileDbContext.class.getName());

    /**
     * Lista de todas las clases mapeadas convertidas a archivos de acceso secuencial.
     */
    private ArrayList<OrmFileDbSet> ormFileDbSets;

    /**
     * Interfaz funcional para hacer la implementacion de sus metodos fueta de la clase y la ejecución de
     * sus metodos dentro de la clase padre
     */
    private IOrmFileBuilderModel iOrmFileModelBuilder;

    /**
     * Objeto que almacena el directorio general
     */
    private File generalDirectory;

    /**
     * Método construntor de clase
     *
     * @param buildModel requerio para crear una instancia, la interfaz funcional OrmFileModelBuilder
     *                   contiene métodos que se debe de declarar su implementación externamente
     * @see IOrmFileBuilderModel
     */
    public OrmFileDbContext(IOrmFileBuilderModel buildModel) {
        this.iOrmFileModelBuilder = buildModel;
        this.generalDirectory = new File(System.getProperty("user.dir") + "\\Database");
        if (!generalDirectory.exists()) {
            if (generalDirectory.mkdirs()) {
                logger.info("Directorio general [" + generalDirectory.getName() + "] creado correctamente");
            } else {
                logger.severe("No se pudo crear el directorio general [" + generalDirectory.getName() + "]");
            }
        }
        OrmFileRecordOperations.setGlobalDirectory(this.generalDirectory);
        ormFileDbSets = this.iOrmFileModelBuilder.mapping();
    }

    /**
     * Método constructor de clase
     *
     * @param buildModel       Interfaz funcional con un método para retornar una lista de objetos de clase para mapèar
     * @param generalDirectory Objeto de tipo File que contiene el directorio general para almacenar los archivos y carpetas que funcionan como persistencia de datos
     */
    public OrmFileDbContext(IOrmFileBuilderModel buildModel, File generalDirectory) throws OrmFileException {
        this.iOrmFileModelBuilder = buildModel;
        ormFileDbSets = this.iOrmFileModelBuilder.mapping();

        OrmFileRecordOperations.setGlobalDirectory(this.generalDirectory);
        if (!generalDirectory.exists()) {
            if (generalDirectory.mkdirs()) {
                logger.info("Directorio general [" + generalDirectory.getName() + "] creado correctamente");
            }
            throw new OrmFileException("El directorio no existe o es un archivo [" + generalDirectory.getPath() + "]");
        }
        this.generalDirectory = generalDirectory;

    }

    @Override
    public IOrmFileOperations select(Class<?> mapped_class) throws OrmFileDbSetException, FileNotFoundException {
        for (OrmFileDbSet dbSet :
                ormFileDbSets) {
            if (dbSet.getClassStructure() == mapped_class) {
                IOrmFileOperations operations = new OrmFileOperations(dbSet.getClassStructure(), generalDirectory);
                mapped_class = null;
                return operations;
            }
        }
        throw new OrmFileDbSetException("La clase mapeada no existe en el contexto");
    }

    @Override
    public IOrmFileDatabase database() {
        IOrmFileDatabase database = new OrmFileDatabase(ormFileDbSets, generalDirectory);
        return database;
    }

    @Override
    public IOrmFileRecord insert(OrmFileRecord record) {
        IOrmFileRecord d = record;
        return d;
    }
}
