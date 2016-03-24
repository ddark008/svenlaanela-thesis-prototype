package org.zeroturnaround.javassist.annotation.processor.test;

import com.google.testing.compile.CompileTester;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

import java.util.Map;

import javax.tools.JavaFileObject;

public class TestMethodPatching extends TestBase {

  @Test
  public void testReplaceMethodWithoutSpecifyingModifyAnnotation() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionNoOverride");

    assertThat(clazz)
      .processedWith(new TypesafeBytecodeModificationProcessor())
      .failsToCompile().withErrorContaining("exists in the original class and must declare @Modify")
      .in(sourceOf(PublicInstanceMethod.class, "ExtensionNoOverride")).onLine(7).atColumn(17);
  }

  @Test
  public void testReplaceMethodThatDoesNotExistInOriginal() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionNonExistentMethod");

    assertThat(clazz)
      .processedWith(new TypesafeBytecodeModificationProcessor())
      .failsToCompile().withErrorContaining("must override or implement a supertype method")
      .in(sourceOf(PublicInstanceMethod.class, "ExtensionNonExistentMethod")).onLine(11).atColumn(17);
  }

  @Test
  public void testReplaceMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionReplaceMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("Hello world!", clazz.transform().construct().method("access", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testBeforeMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionBeforeMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("abcmodnar", clazz.transform().construct().method("access", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testAfterMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionAfterMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("modnarabc", clazz.transform().construct().method("access", new Class<?>[]{String.class}).invoke("random"));
  }
}
