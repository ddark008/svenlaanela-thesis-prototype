package org.zeroturnaround.javassist.annotation.processor.test;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

public class MirrorClassGenerationTests {

  @Test
  public void testTopLevelPublicClass() {
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(JavaFileObjects.forResource(TopLevelPublicVisibility.class.getName().replace('.','/') + "Extension.java"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError().and().generatesFiles(JavaFileObjects.forResource(TopLevelPublicVisibility.class.getName().replace('.','/')+"_Mirror.java"));
  }
  
  @Test
  public void testTopLevelPackageClass() {
    
  }
  
  @Test
  public void testFinalClass() {
    
  }
  
  @Test
  public void testAbstractClass() {
    
  }
  
  @Test
  public void testInterface() {
    
  }
  
}
