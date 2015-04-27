package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import org.zeroturnaround.javassist.annotation.Patches;
import org.zeroturnaround.javassist.annotation.processor.mirror.MirrorClass;
import org.zeroturnaround.javassist.annotation.processor.util.IOUtil;
import org.zeroturnaround.javassist.annotation.processor.validator.ExtensionClassValidator;
import org.zeroturnaround.javassist.annotation.processor.wiring.WiringClass;

/**
 * First round: Generate mirror class to extend from
 * Second pass: Generate Javassist CBP classes based on method implementation
 * detect if we are overriding any methods
 * companion object
 *
 */
@SupportedAnnotationTypes("org.zeroturnaround.javassist.annotation.Patches")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CBPProcessor extends AbstractProcessor {
	
  public CBPProcessor() {
    super();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      for (Element element : roundEnv.getRootElements()) {
        if (element.getKind() == ElementKind.CLASS && element.getAnnotation(Patches.class) != null) {
          doProcess(element);
        }
      }
    }
    catch (RuntimeException e) {
      System.err.println("Generic error running annotation processor " + e);
    }
    return true;
  }

  private void doProcess(Element extensionClass) {
//    ElementUtil.printElement(extensionClass);
    
    TypeMirror originalClassType = getPatchedClassType(extensionClass);
    generateMirrorClass(extensionClass, originalClassType);
    checkExtensionClassValidity(extensionClass, originalClassType);
    generateWiringClass(extensionClass, originalClassType);
  }

  private TypeMirror getPatchedClassType(Element extensionClass) {
    Patches annotation = extensionClass.getAnnotation(Patches.class);
    try {
      Class<?> value = annotation.value();
      return null; // should throw exception instead of returning here;
    }
    catch (MirroredTypeException e) {
      return e.getTypeMirror();
    }
  }
  
  private void generateMirrorClass(Element extensionClass, TypeMirror originalClass) {
    System.out.println("Generating mirror class for " + extensionClass);
    PrintWriter w = null;
    try {
      MirrorClass mirrorClass = new MirrorClass(originalClass.toString());
      
      JavaFileObject mirrorClassObject = processingEnv.getFiler().createSourceFile(mirrorClass.getName(), extensionClass);
      
      w = new PrintWriter(new BufferedWriter(mirrorClassObject.openWriter()));
      w.print(mirrorClass.generateSource());
      w.flush();
    } catch (Exception e) { // TODO: proper error handling
      System.out.println("Failure generating mirror class" + e);
    } finally {
      IOUtil.closeQuietly(w);
    }
  }
  
  private void checkExtensionClassValidity(Element extensionClass, TypeMirror originalClassType) {
    new ExtensionClassValidator(extensionClass, originalClassType, processingEnv.getMessager()).validate();
  }

  private void generateWiringClass(Element extensionClass, TypeMirror originalClass) {    
    System.out.println("Generating wiring class for " + originalClass + " (" + extensionClass + ")");

    PrintWriter w = null;
    try {
      WiringClass wiringClass = new WiringClass(originalClass.toString(), extensionClass.toString());
      
      JavaFileObject cbpClass = processingEnv.getFiler().createSourceFile(wiringClass.getName(), extensionClass);

      w = new PrintWriter(new BufferedWriter(cbpClass.openWriter()));
      w.print(wiringClass.generateSource());
      w.flush();
    } catch (Exception e) {
      System.out.println("Failure generating mirror class" + e);
      e.printStackTrace();
    } finally {
      IOUtil.closeQuietly(w);
    }
  }
}
