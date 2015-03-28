package test;

import org.junit.Test;
import org.junit.Before;

import sample.SampleClass;
import static org.junit.Assert.*;

public class TestClass {
  private SampleClass sampleClass;

  @Before
  public void setup() {
    sampleClass = new SampleClass();
  }
  
  @Test
  public void testInstanceMethod() {
    assertEquals("testInstanceField", sampleClass.instanceAccessor("test"));
  }

  @Test
  public void testStaticMethod() {
    assertEquals("testStaticField", sampleClass.staticAccessor("test"));
  }
}