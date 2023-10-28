package com.ormfile.orm;

import com.ormfile.orm.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Procesa los objetos mapeados y sus retriscciones
 */
public class OrmFileRecord<T> implements IOrmFileRecord<T> {

    /**
     * Lista de objetos mapeados a guardar de tipo genérico
     */
    private ArrayList<T> mapped_objects;

    /**
     * Lista de registros nuevos a guardar en los archivos de base de datos
     */
    private ArrayList<String> newRecordsList;

    /**
     * Método constructor para guardar una lista de objetos mapeados
     *
     * @param mapped_objects Objetos mapeados que se guardaran
     */
    public OrmFileRecord(@NotNull ArrayList<T> mapped_objects) {
        if (!mapped_objects.isEmpty()) {
            this.mapped_objects = mapped_objects;
            newRecordsList = new ArrayList<>();
        }
    }

    /**
     * Método constructor para guardar solamente un objeto mapeado
     */
    public OrmFileRecord(@NotNull T mapped_object) {
        if (mapped_object != null) {
            this.mapped_objects = new ArrayList<>();
            mapped_objects.add(mapped_object);
            newRecordsList = new ArrayList<>();
        }
    }

    @Override
    public boolean save(Class<T> mapped_class) throws IllegalAccessException, IOException {
        String schame = mapped_class.getDeclaredAnnotation(OrmFileEntity.class).schema().isEmpty() ?
                "general" : mapped_class.getDeclaredAnnotation(OrmFileEntity.class).schema();

        String table = mapped_class.getDeclaredAnnotation(OrmFileTable.class).tableName().isEmpty() ?
                mapped_class.getSimpleName() : mapped_class.getDeclaredAnnotation(OrmFileTable.class).tableName();
        processObjects();

        OrmFIleOperationsTools.saveFileTable(OrmFileRecordOperations.getGlobalDirectory(), schame, table, newRecordsList);
        return true;
    }

    /**
     * Método para procesar los objetos mapeados a guardar en archivos de persistencia de datos
     *
     * @throws IOException cuando no se puede acceder a los archivos para comprobar la unicidad de las llaves unicas, llaves foraneas, llaves primarias
     */
    //TODO este método no me gusto como me quedo, REQUERIMIENTO, Refactorizar!!!
    private void processObjects() throws IllegalAccessException, IOException {
        //Interar instancias de clases mapeadas
        int request = 0;       //TODO: quitar esta variable en el futuro
        for (T obj : mapped_objects) {
            //Obtener los Fields de la clase
            Field[] class_properties = obj.getClass().getDeclaredFields();

            //Interar cada Field de la clase para obtener los datos de las propiedades que contiene cada instancia de clase
            for (Field class_property : class_properties) {

                //Obtener annotations del Field (Propiedad de la clase)
                Annotation[] property_annotations = class_property.getAnnotations();

                if (Arrays.stream(property_annotations)
                        .anyMatch(pkAnnotation -> pkAnnotation.equals(class_property.getDeclaredAnnotation(OrmFilePrimaryKey.class)))) {
                    Optional<Annotation> pkAnnotationField = Arrays.stream(property_annotations)
                            .filter(pkAnnotation -> pkAnnotation.equals(class_property.getDeclaredAnnotation(OrmFilePrimaryKey.class)))
                            .findFirst();

                    request = (analyzeConstraints(obj, class_property, pkAnnotationField.orElse(null)));

                } else if (Arrays.stream(property_annotations)
                        .anyMatch(fkAnnotation -> fkAnnotation.equals(class_property.getDeclaredAnnotation(OrmFileForeignKey.class)))) {
                    Optional<Annotation> fkAnnotationField = Arrays.stream(property_annotations)
                            .filter(pkAnnotation -> pkAnnotation.equals(class_property.getDeclaredAnnotation(OrmFileForeignKey.class)))
                            .findFirst();

                    request = (analyzeConstraints(obj, class_property, fkAnnotationField.orElse(null)));
                } else if (Arrays.stream(property_annotations)
                        .anyMatch(columnAnnotation -> columnAnnotation.equals(class_property.getDeclaredAnnotation(OrmFileColumn.class)))) {
                    Optional<Annotation> columnAnnotationField = Arrays.stream(property_annotations)
                            .filter(pkAnnotation -> pkAnnotation.equals(class_property.getDeclaredAnnotation(OrmFileColumn.class)))
                            .findFirst();

                    request = (analyzeConstraints(obj, class_property, columnAnnotationField.orElse(null)));
                } else {
                    break;
                }
            }
            addNewRecordToList(obj, request);
        }

    }

    /**
     * Analizar las restricciones de tabla de cada columna para poder verificar si los datos a insertar son unicos o es un valor de secuencia
     *
     * @param object_instance objeto mapeada como tabla
     * @param column          propiedad de la clase mapedad que actua como columna a la que se le verificara el datos almacenado en el objeto
     * @param annotation      la annotation de la propiedad para verificar si es de tipo PK o UK
     * @return si el valor de retorno en mayor a -1 es un valor de secuencia, si el valor es -1, significa que en valor
     * esta libre para insertar, si el valor es -2, significa que en valor que se desea insertar ya esta ocupado por otro registro
     * @throws IOException si el archivo de datos no es encontrado
     */
    private int analyzeConstraints(T object_instance, Field column, Annotation annotation) throws IOException {
        try {
            if (annotation instanceof OrmFilePrimaryKey) {
                if (((OrmFilePrimaryKey) annotation).isAutoIncrement()) {
                    //Obtenr el nombre de la llave definida en las Annotations
                    String pkName = ((OrmFilePrimaryKey) annotation).pkName().isEmpty() ? column.getName() + "_PK" : ((OrmFilePrimaryKey) annotation).pkName();
                    //Obtener el nuevo número para la secuencia
                    int sequenceNumber = OrmFileRecordOperations.getNewPkSequenceNumber(object_instance.getClass().getName(), pkName);
                    column.setAccessible(true);
                    column.setInt(object_instance, sequenceNumber);
                    column.setAccessible(false);
                } else {
                    column.setAccessible(true);
                    var value = column.get(object_instance);
                    column.setAccessible(false);
                    boolean isUnique = OrmFileRecordOperations.verifyUniqueness(object_instance.getClass(), column, value.toString());
                    if (!isUnique) {
                        throw new UnsupportedOperationException("La operación no pudo completarse debido a una violación de la restricción de clave primaria/única. La entrada con el valor especificado ya existe en la base de datos. Por favor, revise y ajuste los datos proporcionados para garantizar la unicidad y vuelva a intentarlo");
                    }
                }
            } else if (annotation instanceof OrmFileForeignKey) {
                column.setAccessible(true);
                var foreignKeyClassObject = column.get(object_instance);
                column.setAccessible(false);
                for (Field fkField : foreignKeyClassObject.getClass().getDeclaredFields()) {
                    if (fkField.getDeclaredAnnotation(OrmFilePrimaryKey.class) != null) {
                        fkField.setAccessible(true);
                        var fkValue = fkField.get(foreignKeyClassObject);
                        fkField.setAccessible(false);
                        boolean isUnique = OrmFileRecordOperations.verifyUniqueness(foreignKeyClassObject.getClass(), fkField, fkValue.toString());
                        if (isUnique) {
                            throw new UnsupportedOperationException("La columna [" + fkField.getName() + "] de la tabla ["
                                    + object_instance.getClass().getSimpleName() + "] donde se desea insertar el valor ["
                                    + fkValue + "] tiene llave foranea y el valor no existe en la tabla [" + foreignKeyClassObject.getClass().getSimpleName() + "] de la que hereda la llave primaria");
                        }
                    }
                }

            } else if (annotation instanceof OrmFileColumn) {
                if (((OrmFileColumn) annotation).isUnique() && column.getDeclaredAnnotation(OrmFilePrimaryKey.class) == null) {
                    var value = column.get(object_instance);
                    boolean isUnique = OrmFileRecordOperations.verifyUniqueness(object_instance.getClass(), column, value.toString());
                    if (!isUnique) {
                        throw new UnsupportedOperationException("La operación no pudo completarse debido a una violación de la restricción de clave primaria/única. La entrada con el valor especificado ya existe en la base de datos. Por favor, revise y ajuste los datos proporcionados para garantizar la unicidad y vuelva a intentarlo");
                    }
                }
            }
            return -1;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Agregar nuevo regustro a la lista, esta lista existe en RAM, no en archivos persistentes
     *
     * @param obj           objeto del que se construira la linea de registro
     * @param sequenceValue secuencia para asignarsela a una llave primaria
     * @throws IllegalAccessException la cause un Field que no se tiene acceso al valor de la propiedad instanciada
     */
    private void addNewRecordToList(T obj, int sequenceValue) throws IllegalAccessException {
        StringBuilder newLine = new StringBuilder();
        Field[] columns = obj.getClass().getDeclaredFields();
        for (Field column : columns) {

            //Obtener el valor de la propiedad
            column.setAccessible(true);
            var value = column.get(obj);
            column.setAccessible(false);

            //Verificar que tipo de valor se le va a asignar a la columna del registro
            if (column.getDeclaredAnnotation(OrmFileForeignKey.class) != null) {
                //Obtener el valor del objeto que actua como llave foranea
                Arrays.stream(value.getClass().getDeclaredFields()).toList().forEach(field -> {
                    if (field.getDeclaredAnnotation(OrmFilePrimaryKey.class) != null) {
                        try {
                            field.setAccessible(true);
                            var fkValue = field.get(value);
                            field.setAccessible(false);
                            newLine.append(fkValue).append("|");
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                //newLine.append(getForeignKeyValue(obj, column)).append("|");
            } else if (column.getDeclaredAnnotation(OrmFileColumn.class) != null) {
                newLine.append(value).append("|");
            } else {
                newLine.append(value.toString()).append("|");
            }
        }
        newRecordsList.add(newLine.toString().toUpperCase());
    }

//    /**
//     * Obtiene el valor que contiene la llave primaria del objeto foranea que actua como llave forane de la clase mapeada
//     *
//     * @param mapped_obj instancia de clase mapeada
//     * @param value      propiedad de la clase mapeada de la cual se requiere obtener el valor del objeto que actual como llave foranea
//     * @return en valor de tipo cadena que contiene la propiedad de la clase instanciada que actua como llave foranea
//     * @throws IllegalAccessException la cause un Field que no se tiene acceso al valor de la propiedad instanciada
//     */
//    private String getForeignKeyValue(T mapped_obj, Field value) throws IllegalAccessException {
//
//        //Verificar si la propiedad contiene llave foranea
//        if (value.getDeclaredAnnotation(OrmFileForeignKey.class) != null) {
//
//            //Obtener el objeto con el annotation de llave foranea
//            value.setAccessible(true);
//            var foreignKeyInstanceObject = value.get(mapped_obj);
//            value.setAccessible(false);
//
//            //Obtener todas las propiedades del objeto de llave foranea y buscar la llave primaria para obtener si valor
//            for (Field field : foreignKeyInstanceObject.getClass().getDeclaredFields()) {
//                if (field.getDeclaredAnnotation(OrmFilePrimaryKey.class) != null) {
//                    field.setAccessible(true);
//                    var foreignKey_value = field.get(foreignKeyInstanceObject);
//                    return foreignKey_value.toString();
//                }
//            }
//        }
//        return null;
//    }
}


