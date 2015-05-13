package org.zeroturnaround.javassist.annotation.processor.validator;

import java.util.List;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import org.zeroturnaround.javassist.annotation.Modify;

/**
 * Validator for extension class
 * 
 * TODO: should be refactored to throw found exceptions instead of registering these on the Messager object directly.
 */
public class ExtensionClassValidator {
  
  private final Element extensionClass;
  private final TypeMirror originalClass;
  private final Messager messager;

  public ExtensionClassValidator(Element extensionClass, TypeMirror originalClass, Messager messager) {
    this.extensionClass = extensionClass;
    this.originalClass = originalClass;
    this.messager = messager;
  }
  
  public void validate() {
    try {
      ClassPool classPool = ClassPool.getDefault();
      classPool.insertClassPath(new ClassClassPath(this.getClass()));
      CtClass ctClass = classPool.get(originalClass.toString());
      CtField[] existingFields = ctClass.getDeclaredFields();
      CtMethod[] existingMethods = ctClass.getDeclaredMethods();
      CtConstructor[] existingConstructors = ctClass.getDeclaredConstructors();
      
      for (Element element : extensionClass.getEnclosedElements()) {
        boolean shouldExist = false;
        if (element.getAnnotation(Modify.class) != null || element.getAnnotation(Override.class) != null) { // @Modify and @Override annotation elements have to exist in original
          shouldExist = true;
        }
        boolean doesExist = false;
        if (element.getKind() == ElementKind.FIELD) {
          VariableElement variable = (VariableElement) element;
          for (CtField existingField : existingFields) {
            if (existingField.getName().equals(variable.getSimpleName())) {
              doesExist = true;
              break;
            }
          }
        }
        if (element.getKind() == ElementKind.METHOD) {
          ExecutableElement method = (ExecutableElement) element;
          for (CtMethod existingMethod : existingMethods) {
            if (existingMethod.getName().equals(method.getSimpleName().toString()) && compareTypes(existingMethod.getParameterTypes(), method.getParameters())) {
              doesExist = true;
              break;
            }
          }
        }
        if (shouldExist && !doesExist) {
          messager.printMessage(Kind.ERROR, "Element annotated with @Modify does not exist in the original class", element);
        }
        if (!shouldExist && doesExist) {
          messager.printMessage(Kind.ERROR, "Element exists in the original class, @Modify is required", element);
        }
      }
    } catch (Exception e) {
      
    }
  }
  
  /**
   * Returns true if all types represented as CtClass[] elements exist in the list of VariableElement elements. Compares by name
   * 
   * @return
   */
  private boolean compareTypes(CtClass[] first, List<? extends VariableElement> second) {
    if (first == null || second == null) {
      return false;
    }
    if (first.length != second.size()) {
      return false;
    }
    for (int i = 0; i < first.length; i++) {
      if (!first[i].getName().equals(second.get(i).asType().toString())) {
        return false;
      }
    }
    return true;
  }

}
