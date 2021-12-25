package org.lelv.fieldler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.lelv.fieldler.generator.data.ClassData;
import org.lelv.fieldler.generator.data.FieldData;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Objects;

public class FieldComparatorCreator {

  private static final String COMPARE_METHOD = "compare";
  private static final String COMPARATOR_SUFFIX = "FieldComparator";
  private static final String EQUALITY_TESTS_VARIABLE = "equalityTests";
  private static final String EQUALITY_TESTS_METHOD = "createEqualityTests";
  private static final ClassName MAP_CLASS_NAME = ClassName.get("java.util", "Map");
  private static final ClassName BI_PREDICATE_CLASS_NAME = ClassName.get("java.util.function", "BiPredicate");
  private static final ClassName FIELD_COMPARISON_CLASS_NAME = ClassName.get("org.lelv.fieldler.output", "FieldComparison");

  private final ClassData classData;

  private final String objectAName;
  private final String objectBName;
  private final ClassName fieldDataEnumClassName;
  private final ClassName sourceClassClassName;
  private final ParameterizedTypeName mapOfFieldDataAndBiPredicateTypeName;

  public FieldComparatorCreator(ClassData classData, String fieldEnumName) {
    this.classData = classData;
    this.sourceClassClassName = ClassName.bestGuess(classData.getCanonicalName());
    this.fieldDataEnumClassName = ClassName.bestGuess(classData.getPackagePath() + "." + fieldEnumName);
    this.objectAName = Character.toLowerCase(classData.getClassName().charAt(0)) + classData.getClassName().substring(1) + "A";
    this.objectBName = Character.toLowerCase(classData.getClassName().charAt(0)) + classData.getClassName().substring(1) + "B";
    ParameterizedTypeName biPredicateTypeName = ParameterizedTypeName.get(BI_PREDICATE_CLASS_NAME, sourceClassClassName, sourceClassClassName);
    this.mapOfFieldDataAndBiPredicateTypeName = ParameterizedTypeName.get(MAP_CLASS_NAME, fieldDataEnumClassName, biPredicateTypeName);
  }

  public TypeSpec create() {
    return TypeSpec.classBuilder(classData.getClassName() + COMPARATOR_SUFFIX)
                   .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                   .addMethod(privateEmptyConstructor())
                   .addMethod(createCompareMethod())
                   .addMethod(createEqualityTestsMethod())
                   .build();
  }

  private MethodSpec privateEmptyConstructor() {
    return MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();
  }

  private MethodSpec createCompareMethod() {
    ParameterizedTypeName fieldComparisonType = ParameterizedTypeName.get(FIELD_COMPARISON_CLASS_NAME, sourceClassClassName, fieldDataEnumClassName);
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(COMPARE_METHOD)
                                                 .addJavadoc("Compares two objects returning a FieldComparison\n")
                                                 .addJavadoc("@param " + objectAName + " the first object to compare\n")
                                                 .addJavadoc("@param " + objectBName + " the second object to compare\n")
                                                 .addJavadoc(String.format("@throws NullPointerException if {@code %s} or {@code %s} is {@code null}\n",
                                                                           objectAName, objectBName))
                                                 .addJavadoc("@see org.lelv.fieldler.output.FieldComparison\n")
                                                 .addJavadoc("@return FieldComparison")
                                                 .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                                 .returns(fieldComparisonType)
                                                 .addParameter(sourceClassClassName, objectAName)
                                                 .addParameter(sourceClassClassName, objectBName);
    methodBuilder.addStatement("$T.requireNonNull($L)", Objects.class, objectAName);
    methodBuilder.addStatement("$T.requireNonNull($L)", Objects.class, objectBName);
    methodBuilder.addStatement("return new $T<>($L, $L, $L())", FIELD_COMPARISON_CLASS_NAME, objectAName, objectBName, EQUALITY_TESTS_METHOD);
    return methodBuilder.build();
  }

  private MethodSpec createEqualityTestsMethod() {
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(EQUALITY_TESTS_METHOD)
                                                 .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                                                 .returns(mapOfFieldDataAndBiPredicateTypeName);
    methodBuilder.addStatement("$T $L = new $T<>()", mapOfFieldDataAndBiPredicateTypeName, EQUALITY_TESTS_VARIABLE, HashMap.class);
    classData.getFieldsData().forEach(fieldData -> addPredicateTestForField(fieldData, methodBuilder));
    methodBuilder.addStatement("return $L", EQUALITY_TESTS_VARIABLE);
    return methodBuilder.build();
  }

  private void addPredicateTestForField(FieldData fieldData, MethodSpec.Builder methodBuilder) {
    if (!fieldData.isAccessible()) {
      return;
    }
    methodBuilder.addStatement("$L.put($T, ($L, $L) -> $T.equals($L, $L))",
                               EQUALITY_TESTS_VARIABLE,
                               fieldDataEnumClassName.nestedClass(fieldData.getSnakeCaseName()),
                               objectAName, objectBName,
                               java.util.Objects.class,
                               String.format("%s.%s", objectAName, fieldData.getAccess()),
                               String.format("%s.%s", objectBName, fieldData.getAccess()));
  }

}
