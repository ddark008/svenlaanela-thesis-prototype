package org.zeroturnaround.javassist.annotation.processor.validator;

import java.util.List;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import org.zeroturnaround.javassist.annotation.Modify;

/**
 * Validator for extension class
 */
public class ExtensionClassValidator {
  
  private final TypeElement extensionClass;
  private final TypeMirror originalClass;
  private final Messager messager;

  public ExtensionClassValidator(TypeElement extensionClass, TypeMirror originalClass, Messager messager) {
    this.extensionClass = extensionClass;
    this.originalClass = originalClass;
    this.messager = messager;
  }
  
  public void validate() {
    System.out.println("Validating extension class: " + extensionClass.toString());
    try {
      ClassPool classPool = ClassPool.getDefault();
      classPool.insertClassPath(new ClassClassPath(this.getClass()));
      CtClass ctClass = classPool.get(originalClass.toString());
//      CtConstructor[] existingConstructors = ctClass.getDeclaredConstructors();
      
      for (Element element : extensionClass.getEnclosedElements()) {
        System.out.println("Validating existence of: " + element.toString());
        boolean shouldExist = false;
        if (element.getAnnotation(Modify.class) != null || element.getAnnotation(Override.class) != null) {
          shouldExist = true;
        }
        boolean doesExist = false;
        // check if field exists in the original class
        if (element.getKind() == ElementKind.FIELD) {
          VariableElement variable = (VariableElement) element;
          for (CtField existingField : ctClass.getDeclaredFields()) {
            if (existingField.getName().equals(variable.getSimpleName())) {
              doesExist = true;
              break;
            }
          }
          for (CtField existingField : ctClass.getFields()) {
            if (existingField.getName().equals(variable.getSimpleName())) {
              doesExist = true;
              break;
            }
          }
        }
        
        // check if method exists in the original class hierarchy
        if (element.getKind() == ElementKind.METHOD) {
          ExecutableElement method = (ExecutableElement) element;
          for (CtMethod existingMethod : ctClass.getDeclaredMethods()) {
            if (existingMethod.getName().equals(method.getSimpleName().toString()) && compareTypes(existingMethod.getParameterTypes(), method.getParameters())) {
              doesExist = true;
              break;
            }
          }
          for (CtMethod existingMethod : ctClass.getMethods()) {
            if (existingMethod.getName().equals(method.getSimpleName().toString()) && compareTypes(existingMethod.getParameterTypes(), method.getParameters())) {
              doesExist = true;
              break;
            }
          }
          
          // check if method exists in the interface
          for (TypeMirror implementedInterface : extensionClass.getInterfaces()) {
            DeclaredType actualInterface = (DeclaredType) implementedInterface;
            for (Element interfaceElement : actualInterface.asElement().getEnclosedElements()) {
              if (interfaceElement.getKind() == ElementKind.METHOD) {
                ExecutableElement interfaceMethod = (ExecutableElement) interfaceElement;
                if (interfaceMethod.getSimpleName().equals(method.getSimpleName()) && compareTypes(interfaceMethod.getParameters(), method.getParameters())) {
                  doesExist = true;
                  break;
                }
              }
            }
//            annotation.getAnnotationType().
//            for (implementedInterface.) {
//              System.out.println("IFACE METHOD: " + interfaceMethod.getName());
//              if (interfaceMethod.getName().equals(method.getSimpleName().toString()) && compareTypes(interfaceMethod.getParameterTypes(), method.getParameters())) {
//                doesExist = true;
//                break;
//              }
//            }
          }
        }
        
        if (shouldExist && !doesExist) {
          System.out.println("Element " + element.toString() + " does not exist in the original class hierarchy or an interface!");
          messager.printMessage(Diagnostic.Kind.ERROR, "The method/field/constructor "+element.toString()+" of type ... must override or implement a supertype method/field/constructor", element);
        }
        if (!shouldExist && doesExist) {
          System.out.println("Element " + element.toString() + " already exists in the original class hierarchy or an interface!");
          messager.printMessage(Diagnostic.Kind.ERROR, "The method/field/constructor "+element.toString()+" exists in the original class and must declare @Modify or @Override", element);
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
    if (first == null && second == null) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    if (first.length != second.size()) {
      return false;
    }
    for (int i = 0; i < first.length; i++) {
      System.out.println("FIRST: " + first[i].getName() + ", SECOND: " + second.get(i).asType().toString());
      if (!first[i].getName().toString().equals(second.get(i).asType().toString())) {
        return false;
      }
    }
    return true;
  }
  
  private boolean compareTypes(List<? extends VariableElement> first, List<? extends VariableElement> second) {
    if (first == null && second == null) {
      return true;
    }
    if (first == null || second == null) {
      return false;
    }
    if (first.size() != second.size()) {
      return false;
    }
    for (int i = 0; i < first.size(); i++) {
      System.out.println("FIRST: " + first.get(i).asType() + ", SECOND: " + second.get(i).asType());
      if (!first.get(i).asType().toString().equals(second.get(i).asType().toString())) {
        return false;
      }
    }
    return true;
  }

}
