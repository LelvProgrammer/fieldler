package org.lelv.fieldler.generator;

import org.lelv.fieldler.generator.data.ClassData;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FieldlerGenerator {

  public static List<String> createFieldDataAndFieldComparator(ClassData classData, Filer filer) {
    String fieldDataName = createFieldData(classData, filer);
    String fieldComparatorName = createFieldComparator(classData, filer, fieldDataName);
    return Arrays.asList(fieldDataName, fieldComparatorName);
  }

  public static String createFieldData(ClassData classData, Filer filer) {
    TypeSpec fieldDataTypeSpec = FieldDataCreator.create(classData);
    createFile(classData.getPackagePath(), filer, fieldDataTypeSpec);
    return fieldDataTypeSpec.name;
  }

  private static String createFieldComparator(ClassData classData, Filer filer, String fieldsEnumName) {
    FieldComparatorCreator fieldComparatorCreator = new FieldComparatorCreator(classData, fieldsEnumName);
    TypeSpec fieldComparatorTypeSpec = fieldComparatorCreator.create();
    createFile(classData.getPackagePath(), filer, fieldComparatorTypeSpec);
    return fieldComparatorTypeSpec.name;
  }

  private static void createFile(String packageName, Filer filer, TypeSpec typeSpec) {
    JavaFile javaFile = JavaFile
        .builder(packageName, typeSpec)
        .build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new FieldlerGenerationException(e);
    }
  }

}
