package org.lelv.fieldler.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lelv.fieldler.generator.data.ClassData;
import org.lelv.fieldler.generator.data.FieldData;

import javax.lang.model.element.Modifier;

public class FieldDataCreator {

  private static final String FIELD_SUFFIX = "Field";
  private static final String FIELD_NAME_FIELD = "fieldName";

  static TypeSpec create(ClassData classData) {
    TypeSpec.Builder builder = TypeSpec.enumBuilder(classData.getClassName() + FIELD_SUFFIX)
                                       .addModifiers(Modifier.PUBLIC);
    loadEnumValues(classData, builder);
    addFieldNameWithConstructor(builder);
    overrideToString(builder);
    return builder.build();
  }

  private static void loadEnumValues(ClassData classData, TypeSpec.Builder builder) {
    classData.getFieldsData()
            .stream()
            .filter(FieldData::isAccessible)
            .forEach(fieldData -> builder.addEnumConstant(fieldData.getEnumName(),
                    TypeSpec.anonymousClassBuilder("$S", fieldData.getName()).build()));
  }

  private static void addFieldNameWithConstructor(TypeSpec.Builder builder) {
    builder.addField(String.class, FIELD_NAME_FIELD, Modifier.PRIVATE, Modifier.FINAL);
    builder.addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(String.class, FIELD_NAME_FIELD)
                                .addStatement("this.$1L = $1L", FIELD_NAME_FIELD)
                                .build());
  }

  private static void overrideToString(TypeSpec.Builder builder) {
    builder.addMethod(MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return $L", FIELD_NAME_FIELD)
            .build());
  }

}
