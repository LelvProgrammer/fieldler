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
    classData.getFieldsData()
             .stream()
             .filter(FieldData::isAccessible)
             .forEach(fieldData -> builder.addEnumConstant(fieldData.getSnakeCaseName(),
                                                           TypeSpec.anonymousClassBuilder("$S", fieldData.getName()).build()));

    builder.addField(String.class, FIELD_NAME_FIELD, Modifier.PRIVATE, Modifier.FINAL);
    builder.addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .addParameter(String.class, FIELD_NAME_FIELD)
                                .addStatement("this.$1L = $1L", FIELD_NAME_FIELD)
                                .build());

    builder.addMethod(MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(String.class)
                                .addStatement("return $L", FIELD_NAME_FIELD)
                                .build());
    return builder.build();
  }

}
