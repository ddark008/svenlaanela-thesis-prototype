package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.zeroturnaround.javassist.annotation.processor.mirror.MirrorClassGenerator;

import javassist.ClassPool;
import javassist.CtClass;

public class BaseClassGenerationTests {

  protected void testMirrorGeneration(Class<?> clazz) throws Exception {
    testMirrorGeneration(clazz.getName(), false);
  }
  
  protected void testMirrorGeneration(Class<?> clazz, boolean debug) throws Exception {
    testMirrorGeneration(clazz.getName(), debug);
  }
  
  private void testMirrorGeneration(String className, boolean debug) throws Exception {
    CtClass originalCtClass = ClassPool.getDefault().get(className);
    MirrorClassGenerator mirror = new MirrorClassGenerator(originalCtClass);
    String mirrorSrc = mirror.generateSource();
    Assert.assertNotNull(mirrorSrc);
    if (debug) {
      System.out.println(">>>> START CLASS BODY: <<<<");
      System.out.println(mirrorSrc);
      System.out.println(">>>> END CLASS BODY: <<<<");
    }
    String expectedSrc = loadExpectedSource(mirror.getName());
    Assert.assertEquals(expectedSrc, mirrorSrc);
  }
  
  private String loadExpectedSource(String className) throws Exception {
    String fileName = className.replace('.', '/') + ".java";
    StringBuilder result = new StringBuilder();
    BufferedReader reader = null;
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
      if (inputStream == null) {
        Assert.fail("No class source found for: " + fileName);
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line + "\n");
      }
      return result.toString();
    } finally {
      if (reader != null) try { reader.close(); } catch (Exception ignored) {}
    }
  }
}
