package org.zeroturnaround.javassist.annotation.processor.test;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

public class TestMethodPatching extends IntegrationTestBase {

  @Test
  public void testReplaceMethodNoAnnotation() throws Exception {
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(forClassSource(PublicInstanceMethod.class, "ExtensionNoOverride"))
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .failsToCompile().withErrorContaining("exists in the original class and must declare @Modify")
        .in(forClassSource(PublicInstanceMethod.class, "ExtensionNoOverride")).onLine(7).atColumn(17);
  }

  @Test
  public void testReplaceMethodNonExistentMethod() throws Exception {
    Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
        .that(forClassSource(PublicInstanceMethod.class, "ExtensionNonExistentMethod"))
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .failsToCompile().withErrorContaining("must override or implement a supertype method")
        .in(forClassSource(PublicInstanceMethod.class, "ExtensionNonExistentMethod")).onLine(11).atColumn(17);
  }

//  @Test
  public void testReplaceMethodBody() throws Exception {
    extensionOf(PublicInstanceMethod.class)
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forMirrorSourceOf(PublicInstanceMethod.class))
    .and().generatesFiles(forTransformerSourceOf(PublicInstanceMethod.class));

    mirrorOf(PublicInstanceMethod.class)
    .compilesWithoutError();

    transformerOf(PublicInstanceMethod.class)
    .compilesWithoutError();

//    System.out.println("!!!!!!!!!!!!!!!!!");
//
//    Assert.assertEquals("Hello world!", ((PublicInstanceMethod) TestUtil.createInstance(PublicInstanceMethod.class.getName(), PublicInstanceMethodCBP.class.getName())).access("random"));
//    System.out.println(((PublicInstanceMethod) TestUtil.createInstance(PublicInstanceMethod.class.getName(), PublicInstanceMethodCBP.class.getName())).access("random"));
  }
  
}
