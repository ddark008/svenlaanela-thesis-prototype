package org.zeroturnaround.javassist.annotation.processor.test;

import com.google.testing.compile.CompileTester;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;
import org.zeroturnaround.javassist.annotation.processor.test.util.AdHocCompilationResultsClassLoader;

import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

import org.zeroturnaround.javassist.annotation.processor.test.util.TestUtil;

public class TestMethodPatching extends TestBase {

  @Test
  public void testReplaceMethodWithoutSpecifyingModifyAnnotation() throws Exception {
    assertThat(PublicInstanceMethod.class).withSuffix("ExtensionNoOverride")
      .processedWith(new TypesafeBytecodeModificationProcessor())
      .failsToCompile().withErrorContaining("exists in the original class and must declare @Modify")
      .in(sourceOf(PublicInstanceMethod.class, "ExtensionNoOverride")).onLine(7).atColumn(17);
  }

  @Test
  public void testReplaceMethodThatDoesNotExistInOriginal() throws Exception {
    assertThat(PublicInstanceMethod.class).withSuffix("ExtensionNonExistentMethod")
      .processedWith(new TypesafeBytecodeModificationProcessor())
      .failsToCompile().withErrorContaining("must override or implement a supertype method")
      .in(sourceOf(PublicInstanceMethod.class, "ExtensionNonExistentMethod")).onLine(11).atColumn(17);
  }

  @Test
  public void testReplaceMethodBody() throws Exception {
    final Map<String, JavaFileObject> generatedSources = new HashMap<String, JavaFileObject>();
    final Map<String, JavaFileObject> generatedClasses = new HashMap<String, JavaFileObject>();

    assertThat(PublicInstanceMethod.class)
        .withSuffix("ExtensionReplaceMethod")
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesSources().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        generatedSources.putAll(stringJavaFileObjectMap);
      }
    })
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        generatedClasses.putAll(stringJavaFileObjectMap);
      }
    });

    ClassLoader cl = new AdHocCompilationResultsClassLoader(generatedClasses);

    Object patchedObject = TestUtil.createInstance(cl, PublicInstanceMethod.class.getName(), "org.zeroturnaround.javassist.annotation.processor.test.PublicInstanceMethodCBP");
    Assert.assertEquals("Hello world!", patchedObject.getClass().getMethod("access", String.class).invoke(patchedObject, "random"));
  }
}
