package org.zeroturnaround.javassist.annotation.processor.test;

import javax.tools.JavaFileObject;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubject;

public class TestBase {
  static JavaFileObject mirrorSourceOf(Class<?> clazz) {
    return sourceOf(clazz, "_Mirror");
  }

  static JavaFileObject transformerSourceOf(Class<?> clazz) {
    return sourceOf(clazz, "CBP");
  }

  static JavaFileObject sourceOf(Class<?> clazz, String... suffices) {
    return sourceOf(clazz.getName(), suffices);
  }

  static JavaFileObject sourceOf(String className, String... suffices) {
    return JavaFileObjects.forResource(className.replace('.','/') + join(suffices) + ".java");
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

  static ClassAssert assertThat(Class<?> clazz) {
    return new ClassAssert(clazz);
  }

  static JavaSourcesSubject.SingleSourceAdapter extensionOf(Class<?> clazz) {
    return assertThat(clazz).extension();
  }

  static JavaSourcesSubject.SingleSourceAdapter mirrorOf(Class<?> clazz) {
    return assertThat(clazz).mirror();
  }

  static JavaSourcesSubject.SingleSourceAdapter transformerOf(Class<?> clazz) {
    return assertThat(clazz).transformer();
  }

  static JavaSourcesSubject.SingleSourceAdapter assertThat(Class<?> clazz, String... suffices) {
    return assertThat(clazz).withSuffix(suffices);
  }

  static class ClassAssert {
    private String className;
    ClassAssert(Class<?> clazz) {
      this.className = clazz.getName();
    }

    JavaSourcesSubject.SingleSourceAdapter withSuffix(String... suffices) {
      return Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
          .that(sourceOf(className, suffices));
    }

    JavaSourcesSubject.SingleSourceAdapter mirror() {
      return withSuffix("_Mirror");
    }

    JavaSourcesSubject.SingleSourceAdapter extension() {
      return withSuffix("Extension");
    }

    JavaSourcesSubject.SingleSourceAdapter transformer() {
      return withSuffix("CBP");
    }
  }

}
