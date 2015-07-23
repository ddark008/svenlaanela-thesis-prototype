package org.zeroturnaround.javassist.annotation.processor.test;

import javax.tools.JavaFileObject;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;

import ee.lanza.javassist.annotation.processor.usecases.TestUtil;

public class MethodPatchingTests {

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
  public void replaceMethodBody() throws Exception {
    // Test that extension class compiles and generates two files
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(PublicInstanceMethod.class, "Extension"))
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forClassSource(PublicInstanceMethod.class, "_Mirror"))
    .and().generatesFiles(forClassSource(PublicInstanceMethod.class, "CBP"));
    
    // Test that the generated _Mirror class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(PublicInstanceMethod.class, "_Mirror"))
    .compilesWithoutError();
    
    // Test that the generated _Wiring class compiles
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
    .that(forClassSource(PublicInstanceMethod.class, "CBP"))
    .compilesWithoutError();
    
    Assert.assertEquals("Hello world!", ((PublicInstanceMethod) TestUtil.createInstance(PublicInstanceMethod.class.getName(), PublicInstanceMethodCBP.class.getName())).access("random"));
  }
  
}
