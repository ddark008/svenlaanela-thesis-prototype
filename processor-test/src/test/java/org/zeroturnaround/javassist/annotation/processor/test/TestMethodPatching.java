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

  @Test
  public void testAddMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionAddMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("addedMethod!", clazz.transform().construct().method("addedMethod").invoke());
  }

  @Test
  public void testAddMethodCallFromExisting() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicInstanceMethod.class, "ExtensionAddMethodCallFromExisting");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        clazz.appendToClassPath(stringJavaFileObjectMap);
      }
    });

    Assert.assertEquals("addedMethodCalledFromExisting!", clazz.transform().construct().method("access", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testReplacePrivateMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PrivateInstanceMethod.class, "ExtensionReplaceMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("Hello world!", clazz.transform().construct().method("accessPrivate", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testBeforePrivateMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PrivateInstanceMethod.class, "ExtensionBeforeMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("xyzdomran", clazz.transform().construct().method("accessPrivate", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testAfterPrivateMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PrivateInstanceMethod.class, "ExtensionAfterMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      @Override public void accept(Map<String, JavaFileObject> javaFileObjects) {
        clazz.appendToClassPath(javaFileObjects);
      }
    });

    Assert.assertEquals("domranxyz", clazz.transform().construct().method("accessPrivate", new Class<?>[]{String.class}).invoke("random"));
  }

  @Test
  public void testReplaceStaticMethodWithoutSpecifyingModifyAnnotation() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicStaticMethod.class, "ExtensionNoOverride");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .failsToCompile().withErrorContaining("exists in the original class and must declare @Modify")
        .in(sourceOf(PublicStaticMethod.class, "ExtensionNoOverride")).onLine(7).atColumn(17);
  }

  @Test
  public void testReplaceStaticMethodThatDoesNotExistInOriginal() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicStaticMethod.class, "ExtensionNonExistentMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .failsToCompile().withErrorContaining("must override or implement a supertype method")
        .in(sourceOf(PublicStaticMethod.class, "ExtensionNonExistentMethod")).onLine(11).atColumn(17);
  }

  @Test
  public void testReplaceStaticMethod() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicStaticMethod.class, "ExtensionReplaceMethod");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        clazz.appendToClassPath(stringJavaFileObjectMap);
      }
    });

    Assert.assertEquals("replaced public static", clazz.transform().method("access", new Class[]{String.class}).invoke("public static"));
  }

  @Test
  public void testAddStaticMethodCallFromExisting() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicStaticMethod.class, "ExtensionAddMethodCallFromExisting");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        clazz.appendToClassPath(stringJavaFileObjectMap);
      }
    });

    Assert.assertEquals("dohtem dedda", clazz.transform().method("access", new Class[]{String.class}).invoke("added method"));
  }

  @Test
  public void testModifyThrownException() throws Exception {
    final TransformableClassDefinition clazz = new TransformableClassDefinition(PublicExceptionMethod.class, "ExtensionModifyException");

    assertThat(clazz)
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError()
        .and().generatesClasses().forAllOfWhich(new CompileTester.CompilationResultsConsumer() {
      public void accept(Map<String, JavaFileObject> stringJavaFileObjectMap) {
        clazz.appendToClassPath(stringJavaFileObjectMap);
      }
    });

    Assert.assertEquals("modified exception", clazz.transform().construct().method("access", new Class[]{}).invoke());
  }
}
