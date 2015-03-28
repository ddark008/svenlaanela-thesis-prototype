package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.zeroturnaround.javassist.annotation.MethodCall;
import org.zeroturnaround.javassist.annotation.Patches;
import org.zeroturnaround.javassist.annotation.processor.model.OriginalClass;

/**
 * First pass: Generate mirror classes for type safety
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

  private void printElement(Element element) {
    System.out.println(printElement(element, 0));
  }

  private String printElement(Element element, int offset) {
    String print = "";
    for (int i = 0; i < offset; i++) {
      print += "  ";
    }
    print += element + ", " + element.asType() + "\n";
    if (element instanceof ExecutableElement) {
      ExecutableElement executableElement = ((ExecutableElement) element);
      for (int i = 0; i < offset; i++) {
        print += "  ";
      }
      print += "Parameters:\n";
      for (VariableElement paramElement : executableElement.getParameters()) {
        print += printElement(paramElement, offset + 1);
      }
      for (int i = 0; i < offset; i++) {
        print += "  ";
      }
      print += "TypeParameters:\n";
      for (TypeParameterElement typeParamElement : executableElement.getTypeParameters()) {
        print += printElement(typeParamElement, offset + 1);

      }
    }
    for (Element subElement : element.getEnclosedElements()) {
      print += printElement(subElement, offset + 1);
    }
    return print + "\n";
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "WTF!");
      if (!roundEnv.processingOver()) {
        for (Element element : roundEnv.getRootElements()) {
          if (element.getKind() == ElementKind.CLASS && element.getAnnotation(Patches.class) != null) {
            printElement(element);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Patching class: " + element.getSimpleName(), element);

            Patches annotation = element.getAnnotation(Patches.class);
            try {
              Class<?> value = annotation.value();
            }
            catch (MirroredTypeException e) {
              generateMirrorClass(element, e.getTypeMirror());
              generateCBPClass(e.getTypeMirror(), element.asType());
            }

          }
        }
        return true;
      }
      else {
        return false;
      }
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "WTF2!");
      return false;
    }
    // return false;
  }
	
  // TODO: Refactor to separate CBP generator class and add unit tests
  // TODO: Companion class should be transformed to an inner class of the patched class during annotation processing.
  private void generateCBPClass(TypeMirror originalClass, TypeMirror companionClass) {

    System.out.println("Generating CBP for class: " + originalClass);
    ClassPool classPool = new ClassPool();
    classPool.insertClassPath(new ClassClassPath(this.getClass()));

    try {
      final CtClass original = classPool.get(originalClass.toString());

      JavaFileObject cbpClass = processingEnv.getFiler().createSourceFile(original.getName() + "CBP", null);
      PrintWriter w = new PrintWriter(new BufferedWriter(cbpClass.openWriter()));

      OriginalClass oc = new OriginalClass();
      oc.packageName = original.getPackageName();
      oc.name = original.getName();
      oc.cbpName = original.getName() + "CBP";
      oc.cbpSimpleName = original.getSimpleName() + "CBP";
      oc.companion.name = companionClass.toString();

      Velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
      Velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

      Context ctx = new VelocityContext();
      ctx.put("lol", "wat");
      ctx.put("original", oc);
      ctx.put("companion", oc.companion);
      Velocity.mergeTemplate("cbp.vtl", "utf-8", ctx, w);

      w.close();
    }
    catch (NotFoundException e) {
      // processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
      System.out.println(">> NotFoundException");
      e.printStackTrace();
    }
    catch (IOException e) {
      // processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
      System.out.println(">> IOException");
      e.printStackTrace();
    }
    catch (Throwable e) {
      System.out.println(">> Throwable" + e.getMessage());
    }

    System.out.println("Finished generating CBP for class: " + originalClass);
  }
  
  private void generateMirrorClass(Element element, TypeMirror originalClass) {

    PrintWriter w = null;
    try {
      MirrorClass mirrorClass = new MirrorClass(originalClass.toString());

      JavaFileObject mirrorClassObject = processingEnv.getFiler().createSourceFile(mirrorClass.getName(), element);

      w = new PrintWriter(new BufferedWriter(mirrorClassObject.openWriter()));
      w.print(mirrorClass.generateSource());
      w.flush();
      w.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("failure generating class");
    }
    finally {
      if (w != null) try { w.close(); } catch (Exception ignored) {}
    }
  }
	
}
