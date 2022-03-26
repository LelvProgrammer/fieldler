package org.lelv.fieldler.processor;

import com.google.auto.service.AutoService;
import org.lelv.fieldler.annotation.FieldComparator;
import org.lelv.fieldler.generator.FieldlerGenerationException;
import org.lelv.fieldler.generator.FieldlerGenerator;
import org.lelv.fieldler.generator.data.ClassData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.lelv.fieldler.annotation.FieldData",
                           "org.lelv.fieldler.annotation.FieldComparator"})
public class FieldlerAnnotationProcessor extends AbstractProcessor {

  private enum Generator {FIELD_DATA, FIELD_COMPARATOR}

  private final Set<Element> processedElements = new HashSet<>();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    printInfo("[Fieldler] Starting Annotation Processing");
    for (TypeElement annotation : annotations) {
      roundEnv.getElementsAnnotatedWith(annotation)
              .stream()
              .filter(element -> !processedElements.contains(element))
              .peek(processedElements::add)
              .forEach(this::processElement);
    }
    return true;
  }

  private void processElement(Element element) {
    if (canProcessElement(element)) {
      ClassData classData = ClassProcessor.processClass(element);
      if (classData.hasAccessibleFields()) {
        tryGenerateClasses(classData, getGenerator(element));
      } else {
        printWarning("[Fieldler] Element has no accessible fields, no classes will be generated for: " + element);
      }
    } else {
      printWarning("[Fieldler] Can not process element: " + element);
    }
  }

  private boolean canProcessElement(Element element) {
    return element.getKind() == ElementKind.CLASS && element.getModifiers().contains(Modifier.PUBLIC);
  }

  private Generator getGenerator(Element element) {
    return element.getAnnotation(FieldComparator.class) == null ? Generator.FIELD_DATA : Generator.FIELD_COMPARATOR;
  }

  private void tryGenerateClasses(ClassData classData, Generator generator) {
    try {
      Object response;
      switch (generator) {
        case FIELD_DATA:
          response = createFieldData(classData);
          break;
        case FIELD_COMPARATOR:
          response = createFieldComparator(classData);
          break;
        default:
          throw new IllegalArgumentException("Unknown generator " + generator);
      }
      printInfo(String.format("[Fieldler] Generated %s for class %s", response, classData.getCanonicalName()));
    } catch (FieldlerGenerationException e) {
      printWarning("[Fielder] Error generating classes - Detail: " + e.getMessage());
    }
  }

  private String createFieldData(ClassData classData) {
    return FieldlerGenerator.createFieldData(classData, processingEnv.getFiler());
  }

  private List<String> createFieldComparator(ClassData classData) {
    return FieldlerGenerator.createFieldDataAndFieldComparator(classData, processingEnv.getFiler());
  }

  private void printInfo(String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
  }

  private void printWarning(String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
  }
}
