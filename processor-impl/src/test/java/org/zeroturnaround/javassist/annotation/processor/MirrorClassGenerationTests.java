package org.zeroturnaround.javassist.annotation.processor;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.testclasses.TestFinalMethod;
import org.zeroturnaround.javassist.annotation.processor.testclasses.TestPrivateMethod;
import org.zeroturnaround.javassist.annotation.processor.testclasses.TestSimple;

public class MirrorClassGenerationTests extends BaseClassGenerationTests {
  
  @Test
  public void testSimple() throws Exception {
    testMirrorGeneration(TestSimple.class);
  }
  
  @Test
  public void testPrivateMethod() throws Exception {
    testMirrorGeneration(TestPrivateMethod.class);
  }
  
  @Test
  public void testFinalMethod() throws Exception {
    testMirrorGeneration(TestFinalMethod.class);
  }
}
