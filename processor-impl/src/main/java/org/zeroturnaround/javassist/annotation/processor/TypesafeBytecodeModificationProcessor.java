package org.zeroturnaround.javassist.annotation.processor;

import javassist.ClassPath;
import javassist.LoaderClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.javassist.annotation.Patches;
import org.zeroturnaround.javassist.annotation.processor.mirror.MirrorClassGenerator;
import org.zeroturnaround.javassist.annotation.processor.mirror.MirrorClassRegistry;
import org.zeroturnaround.javassist.annotation.processor.util.IOUtil;
import org.zeroturnaround.javassist.annotation.processor.validator.ExtensionClassValidator;
import org.zeroturnaround.javassist.annotation.processor.version.VersionRange;
import org.zeroturnaround.javassist.annotation.processor.wiring.WiringClassGenerator;

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

import javassist.ClassPool;
import javassist.CtClass;

@SupportedAnnotationTypes("org.zeroturnaround.javassist.annotation.Patches")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TypesafeBytecodeModificationProcessor extends AbstractProcessor {
  private static Logger logger = LoggerFactory.getLogger(TypesafeBytecodeModificationProcessor.class);

  private MirrorClassRegistry mirrorClassRegistry = new MirrorClassRegistry();

  private static final ClassPool classPool;

  static {
    classPool = new ClassPool(true);
  }
  
  public TypesafeBytecodeModificationProcessor() {
    // Add classPath of compiling sources (it's dependencies)
    ClassLoader cl = this.getClass().getClassLoader();
    classPool.appendClassPath(new LoaderClassPath(cl));
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getRootElements()) {
      if (element.getKind() == ElementKind.CLASS && element.getAnnotation(Patches.class) != null) {
        doProcess((TypeElement) element);
      }
    }
    return true;
  }

  private void doProcess(TypeElement extensionClass) {
    try {
      logger.info("");
      TypeMirror originalClassType = getPatchedClassType(extensionClass);

      logger.info("@Patches(" + originalClassType.toString()+")");
      logger.info("public class " + extensionClass);

      generateMirrorClass(extensionClass, originalClassType);
      validateExtensionClass(extensionClass, originalClassType);
      generateWiringClass(extensionClass, originalClassType);
    } catch (RuntimeException e) {
      logger.error("Uncaught error running type-safe bytecode modification processor for class : " + extensionClass, e);
    }
  }

  private TypeMirror getPatchedClassType(Element extensionClass) {
    Patches annotation = extensionClass.getAnnotation(Patches.class);
    try {
      Class<?> value = annotation.value();
      throw new RuntimeException("Should not occur!");
    }
    catch (MirroredTypeException e) {
      return e.getTypeMirror();
    }
  }
  
  private void generateMirrorClass(Element extensionClass, TypeMirror originalClass) {
    PrintWriter w = null;
    try {
      CtClass originalCtClass = classPool.getCtClass(originalClass.toString());
      MirrorClassGenerator mirrorClass = new MirrorClassGenerator(originalCtClass);
      if (mirrorClassRegistry.contains(mirrorClass.getName())) {
        logger.info("Existing mirror class: " + mirrorClassRegistry.getMirror(mirrorClass.getName()));
        return;
      } else {
        logger.info("Generating mirror class: " + mirrorClass.getName());
      }
      JavaFileObject mirrorClassObject = processingEnv.getFiler().createSourceFile(mirrorClass.getName(), extensionClass);
      
      w = new PrintWriter(new BufferedWriter(mirrorClassObject.openWriter()));
      w.print(mirrorClass.generateSource());
      w.flush();
      logger.info("Finished: " + mirrorClassObject.toUri());
    } catch (Exception e) {
      logger.error("Failure generating mirror class for: " + originalClass.toString(), e);
    } finally {
      IOUtil.closeQuietly(w);
    }
  }
  
  private void validateExtensionClass(TypeElement extensionClass, TypeMirror originalClassType) {
    logger.info("Validating extension class");
    new ExtensionClassValidator(classPool, extensionClass, originalClassType, processingEnv.getMessager()).validate();
  }

  private void generateWiringClass(Element extensionClass, TypeMirror originalClass) {    
    WiringClassGenerator wiringClass = new WiringClassGenerator(classPool, originalClass.toString(), extensionClass.toString());
    logger.info("Generating wiring class: " + wiringClass.getName());

    PrintWriter w = null;
    try {
      JavaFileObject cbpClass = processingEnv.getFiler().createSourceFile(wiringClass.getName(), extensionClass);

      w = new PrintWriter(new BufferedWriter(cbpClass.openWriter()));
      w.print(wiringClass.generateSource());
      w.flush();
    } catch (Exception e) {
      logger.error("Failure generating wiring class: " + wiringClass.getName() , e);
      e.printStackTrace();
    } finally {
      IOUtil.closeQuietly(w);
    }
  }
}
