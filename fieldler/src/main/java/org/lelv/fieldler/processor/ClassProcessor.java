package org.lelv.fieldler.processor;

import org.lelv.fieldler.generator.data.ClassData;
import org.lelv.fieldler.generator.data.FieldData;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ClassProcessor {

  private static final String IS_PREFIX = "is";
  private static final String METHOD_SUFFIX = "()";
  private static final String GETTER_PREFIX = "get";
  private static final String OBJECT = Object.class.getCanonicalName();
  private static final String BOOLEAN_OBJECT = Boolean.class.getCanonicalName();
  private static final String BOOLEAN_PRIMITIVE = boolean.class.getSimpleName();

  public static ClassData processClass(Element classElement) {
    String canonicalName = classElement.toString();
    List<FieldData> fieldData = fieldData(classElement);
    return new ClassData(canonicalName, fieldData);
  }

  private static List<FieldData> fieldData(Element classElement) {
    List<VariableElement> fields = getEnclosedElements(classElement, VariableElement.class);
    List<ExecutableElement> methods = getEnclosedElements(classElement, ExecutableElement.class);
    Set<String> methodNames = getPublicAndNoParameterMethodNames(methods);
    return buildFieldData(fields, methodNames);
  }

  private static <T> List<T> getEnclosedElements(Element element, Class<T> clazz) {
    List<T> result = new ArrayList<>();
    Element currentElement = element;
    while (currentElement != null) {
      List<T> fields = currentElement.getEnclosedElements()
                                     .stream()
                                     .filter(clazz::isInstance)
                                     .filter(enclosedElement -> !enclosedElement.getModifiers().contains(Modifier.STATIC))
                                     .map(clazz::cast)
                                     .collect(Collectors.toList());
      result.addAll(fields);
      currentElement = getSuperClass(currentElement);
    }
    return result;
  }

  private static Element getSuperClass(Element element) {
    if (!(element instanceof TypeElement)) {
      return null;
    }
    TypeElement typeElement = (TypeElement) element;
    if (!(typeElement.getSuperclass() instanceof DeclaredType)) {
      return null;
    }
    Element parentElement = ((DeclaredType) typeElement.getSuperclass()).asElement();
    if (OBJECT.equals(parentElement.toString())) {
      return null;
    }
    return parentElement;
  }

  private static Set<String> getPublicAndNoParameterMethodNames(List<ExecutableElement> methodElements) {
    return methodElements.stream()
                         .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
                         .filter(method -> method.getParameters().size() == 0)
                         .map(ExecutableElement::getSimpleName)
                         .map(Object::toString)
                         .collect(Collectors.toSet());
  }

  private static List<FieldData> buildFieldData(List<VariableElement> fields, Set<String> methodNames) {
    List<FieldData> result = new ArrayList<>();
    for (VariableElement field : fields) {
      String access;
      String fieldName = field.getSimpleName().toString();
      if (canAccessDirectlyField(field)) {
        access = fieldName;
      } else {
        access = accessMethodName(field, methodNames);
      }
      result.add(new FieldData(fieldName, access));
    }
    return result;
  }

  private static boolean canAccessDirectlyField(Element field) {
    return field.getModifiers().contains(Modifier.PUBLIC);
  }

  private static String accessMethodName(VariableElement field, Set<String> methodNames) {
    for (String possibleMethodName : acceptedMethodNames(field)) {
      if (methodNames.contains(possibleMethodName)) {
        return possibleMethodName + METHOD_SUFFIX;
      }
    }
    return null;
  }

  private static List<String> acceptedMethodNames(VariableElement field) {
    String fieldName = field.toString();
    List<String> methodNames = new ArrayList<>();
    if (isBoolean(field)) {
      methodNames.add(methodName(IS_PREFIX, fieldName));
    }
    methodNames.add(methodName(GETTER_PREFIX, fieldName));
    methodNames.add(fieldName);
    return methodNames;
  }

  private static boolean isBoolean(VariableElement field) {
    String fieldType = field.asType().toString();
    return BOOLEAN_PRIMITIVE.equals(fieldType) || BOOLEAN_OBJECT.equals(fieldType);
  }

  private static String methodName(String prefix, String fieldName) {
    return prefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
  }

}
