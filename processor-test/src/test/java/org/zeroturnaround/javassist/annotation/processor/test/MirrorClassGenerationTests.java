package org.zeroturnaround.javassist.annotation.processor.test;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class MirrorClassGenerationTests {

  @Test
  public void testTopLevelPublicClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPublic.class.getName().replace('.','/') + "Extension.java"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelPublic.class.getName().replace('.','/')+"_Mirror.java"))
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelPublic.class.getName().replace('.','/')+"CBP.java"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPublic.class.getName().replace('.','/') + "_Mirror.java"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPublic.class.getName().replace('.','/')+"CBP.java"))
    .compilesWithoutError();
    
    // Should we test that the generated Wiring class also does something?
  }
  
  @Test
  public void testTopLevelPackageClass() {
 // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPackage.class.getName().replace('.','/') + "Extension.java"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelPackage.class.getName().replace('.','/')+"_Mirror.java"))
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelPackage.class.getName().replace('.','/')+"CBP.java"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPackage.class.getName().replace('.','/') + "_Mirror.java"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPackage.class.getName().replace('.','/')+"CBP.java"))
    .compilesWithoutError();
  }
  
  @Test
  public void testTopLevelFinalClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelFinal.class.getName().replace('.','/') + "Extension.java"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelFinal.class.getName().replace('.','/')+"_Mirror.java"))
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelFinal.class.getName().replace('.','/')+"CBP.java"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelFinal.class.getName().replace('.','/') + "_Mirror.java"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelFinal.class.getName().replace('.','/')+"CBP.java"))
    .compilesWithoutError();
  }
  
  @Test
  public void testAbstractClass() {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelAbstract.class.getName().replace('.','/') + "Extension.java"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelAbstract.class.getName().replace('.','/')+"_Mirror.java"))
    .and().generatesFiles(JavaFileObjects.forResource(TopLevelAbstract.class.getName().replace('.','/')+"CBP.java"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelAbstract.class.getName().replace('.','/') + "_Mirror.java"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelAbstract.class.getName().replace('.','/')+"CBP.java"))
    .compilesWithoutError();
  }
  
  @Test
  public void testInterface() {
    
  }
  
}
