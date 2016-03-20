package org.zeroturnaround.javassist.annotation.processor.test;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

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
    assertThat(PublicInstanceMethod.class).withSuffix("ExtensionReplaceMethod")
      .processedWith(new TypesafeBytecodeModificationProcessor())
      .compilesWithoutError()
      .and().generatesFiles(mirrorSourceOf(PublicInstanceMethod.class))
      .and().generatesFiles(transformerSourceOf(PublicInstanceMethod.class));

    assertThat(PublicInstanceMethod.class).mirror()
      .compilesWithoutError();

    assertThat(PublicInstanceMethod.class).transformer()
      .compilesWithoutError();

//    Assert.assertEquals("Hello world!", ((PublicInstanceMethod) TestUtil.createInstance(PublicInstanceMethod.class.getName(), PublicInstanceMethodCBP.class.getName())).access("random"));
//    System.out.println(((PublicInstanceMethod) TestUtil.createInstance(PublicInstanceMethod.class.getName(), PublicInstanceMethodCBP.class.getName())).access("random"));

  }
  
}
