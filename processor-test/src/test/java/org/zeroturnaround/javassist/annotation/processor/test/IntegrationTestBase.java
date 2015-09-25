package org.zeroturnaround.javassist.annotation.processor.test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubject;

import javax.tools.JavaFileObject;

/**
 * Created by lanza on 25/09/15.
 */
public class IntegrationTestBase {
  static JavaFileObject forMirrorSourceOf(Class<?> clazz) {
    return forClassSource(clazz, "_Mirror");
  }

  static JavaFileObject forTransformerSourceOf(Class<?> clazz) {
    return forClassSource(clazz, "CBP");
  }

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

  static JavaSourcesSubject.SingleSourceAdapter extensionOf(Class<?> clazz) {
    return Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(forClassSource(clazz, "Extension"));
  }

  static JavaSourcesSubject.SingleSourceAdapter mirrorOf(Class<?> clazz) {
    return Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(forClassSource(clazz, "_Mirror"));
  }

  static JavaSourcesSubject.SingleSourceAdapter transformerOf(Class<?> clazz) {
    return Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(forClassSource(clazz, "CBP"));
  }
}
