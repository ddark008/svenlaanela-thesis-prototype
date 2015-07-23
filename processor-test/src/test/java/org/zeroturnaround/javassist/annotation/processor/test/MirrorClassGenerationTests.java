package org.zeroturnaround.javassist.annotation.processor.test;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class MirrorClassGenerationTests {
  
  static JavaFileObject forClassSource(Class<?> clazz, String... suffices) {
    return JavaFileObjects.forResource(clazz.getName().replace('.','/') + join(suffices) + ".java");
  }
  
  static JavaFileObject forClass(Class<?> clazz, String... suffices) {
    return JavaFileObjects.forResource(clazz.getName().replace('.','/') + join(suffices) + ".class");    
  }
  
  private static String join(String... suffices) {
    String suffix = "";
    if (suffices != null) {
      for (String suff : suffices) {
        suffix += suff;
      }
    }
    return suffix;
  }

  @Test
  public void testTopLevelPublicClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPublic.class, "Extension"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forClassSource(TopLevelPublic.class, "_Mirror"))
    .and().generatesFiles(forClassSource(TopLevelPublic.class, "CBP"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPublic.class, "_Mirror"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPublic.class, "CBP"))
    .compilesWithoutError();
    
    // Should we test that the generated Wiring class also does something?
  }
  
  @Test
  public void testTopLevelPackageClass() {
 // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPackage.class, "Extension"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forClassSource(TopLevelPackage.class, "_Mirror"))
    .and().generatesFiles(forClassSource(TopLevelPackage.class, "CBP"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPackage.class, "_Mirror"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelPackage.class, "CBP"))
    .compilesWithoutError();
  }
  
  @Test
  public void testTopLevelFinalClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelFinal.class, "Extension"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forClassSource(TopLevelFinal.class, "_Mirror"))
    .and().generatesFiles(forClassSource(TopLevelFinal.class, "CBP"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelFinal.class, "_Mirror"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelFinal.class, "CBP"))
    .compilesWithoutError();
  }
  
  @Test
  public void testAbstractClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelAbstract.class, "Extension"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forClassSource(TopLevelAbstract.class, "_Mirror"))
    .and().generatesFiles(forClassSource(TopLevelAbstract.class, "CBP"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelAbstract.class, "_Mirror"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(TopLevelAbstract.class, "CBP"))
    .compilesWithoutError();
  }
  
  @Test
  public void testInterface() {
    
  }
  
}
