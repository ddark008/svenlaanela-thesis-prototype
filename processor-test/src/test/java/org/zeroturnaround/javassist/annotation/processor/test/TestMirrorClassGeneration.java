package org.zeroturnaround.javassist.annotation.processor.test;

import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;

public class TestMirrorClassGeneration extends IntegrationTestBase {

  @Test
  public void testTopLevelPublicClass() {
    extensionOf(TopLevelPublic.class)
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError();

    mirrorOf(TopLevelPublic.class)
    .compilesWithoutError();

    transformerOf(TopLevelPublic.class)
    .compilesWithoutError();
  }
  
  @Test
  public void testTopLevelPackageClass() {
    extensionOf(TopLevelPackage.class)
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forMirrorSourceOf(TopLevelPackage.class))
    .and().generatesFiles(forTransformerSourceOf(TopLevelPackage.class));

    System.out.println(forMirrorSourceOf(TopLevelPackage.class).toUri());

    mirrorOf(TopLevelPackage.class)
    .compilesWithoutError();

    transformerOf(TopLevelPackage.class)
    .compilesWithoutError();
  }
  
  @Test
  public void testTopLevelFinalClass() {
    extensionOf(TopLevelFinal.class)
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forMirrorSourceOf(TopLevelFinal.class))
    .and().generatesFiles(forTransformerSourceOf(TopLevelFinal.class));

    mirrorOf(TopLevelFinal.class)
    .compilesWithoutError();

    transformerOf(TopLevelFinal.class)
    .compilesWithoutError();
  }
  
  @Test
  public void testAbstractClass() {
    extensionOf(TopLevelAbstract.class)
    .processedWith(new TypesafeBytecodeModificationProcessor())
    .compilesWithoutError()
    .and().generatesFiles(forMirrorSourceOf(TopLevelAbstract.class))
    .and().generatesFiles(forTransformerSourceOf(TopLevelAbstract.class));
    
    mirrorOf(TopLevelAbstract.class)
    .compilesWithoutError();

    transformerOf(TopLevelAbstract.class)
    .compilesWithoutError();
  }
  
  @Test
  public void testInterface() {
    
  }
  
}
