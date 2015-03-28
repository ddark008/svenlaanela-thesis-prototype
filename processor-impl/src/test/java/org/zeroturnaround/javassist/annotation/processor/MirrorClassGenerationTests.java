package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.testclasses.TestSimple;

public class MirrorClassGenerationTests {
  
  @Test
  public void testSimple() throws Exception {
    String className = TestSimple.class.getName();
    MirrorClass mirror = new MirrorClass(className);
    String mirrorSrc = mirror.generateSource();
    Assert.assertNotNull(mirrorSrc);
    System.out.println(mirrorSrc);
    String expectedSrc = loadExpectedSource(mirror.getName().replace('.', '/'));
    Assert.assertEquals(expectedSrc, mirrorSrc);
  }
  
  private String loadExpectedSource(String fileName) throws Exception {
    StringBuilder result = new StringBuilder();
    BufferedReader reader = null;
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName + ".java");
      System.out.println(fileName + ".java");
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
