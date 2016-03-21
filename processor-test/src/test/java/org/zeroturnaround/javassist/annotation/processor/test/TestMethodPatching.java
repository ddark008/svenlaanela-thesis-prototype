package org.zeroturnaround.javassist.annotation.processor.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.tools.JavaFileObject;

import org.junit.Assert;
import org.junit.Test;
import org.zeroturnaround.javassist.annotation.processor.TypesafeBytecodeModificationProcessor;
import com.google.testing.compile.CompileTester;

import ee.lanza.javassist.annotation.processor.usecases.TestUtil;

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
    CompileTester.SuccessfulCompilationClause compilationResult = assertThat(PublicInstanceMethod.class).withSuffix("ExtensionReplaceMethod")
        .processedWith(new TypesafeBytecodeModificationProcessor())
        .compilesWithoutError();

    Collection<JavaFileObject> results = compilationResult.generatedSources();
    Collection<JavaFileObject> classes = compilationResult.generatedClasses();

    JavaFileObject mirror = null;
    JavaFileObject transformer = null;

    for (JavaFileObject jfo : results) {
      if (jfo.getName().endsWith("_Mirror.java")) {
        mirror = jfo;
      }
      if (jfo.getName().endsWith("CBP.java")) {
        transformer = jfo;
      }
    }

    compilationResult.and().generatesFiles(mirror).and().generatesFiles(transformer);

    MagicClassLoader mcl = new MagicClassLoader(classes);

    Object patchedObject = TestUtil.createInstance(mcl, PublicInstanceMethod.class.getName(), "org.zeroturnaround.javassist.annotation.processor.test.PublicInstanceMethodCBP");
    Assert.assertEquals("Hello world!", patchedObject.getClass().getMethod("access", String.class).invoke(patchedObject, "random"));
  }

  private class MagicClassLoader extends ClassLoader {
    private final Map<String, JavaFileObject> classes;
    private final Map<String, JavaFileObject> resources;


    MagicClassLoader(Collection<JavaFileObject> jfos) {
      Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();
      Map<String, JavaFileObject> resources = new HashMap<String, JavaFileObject>();
      for (JavaFileObject jfo : jfos) {
        String name = jfo.getName();
        if (name.startsWith("/CLASS_OUTPUT/")) {
          name = name.substring(14);
        }
        resources.put(name, jfo);
        name = name.replace("/", ".");
        if (name.endsWith(".class")) {
          name = name.substring(0, name.length()-6);
        }
        classes.put(name, jfo);
      }
      this.classes = classes;
      this.resources = resources;
    }

    @Override
    public URL getResource(String name) {
      if (resources.containsKey(name)) {
        return getParent().getResource("java/lang/Object.class");
      } else {
        return getParent().getResource(name);
      }
    }

    public InputStream getResourceAsStream(String name) {
      JavaFileObject jfo = resources.get(name);
      if (jfo != null) {
        try {
          return jfo.openInputStream();
        } catch (Exception e) {
          return getParent().getResourceAsStream(name);
        }
      }
      return getParent().getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      JavaFileObject jfo = classes.remove(name);
      if (jfo != null) {
        // TODO: use IOUtils instead!
        ByteArrayOutputStream bas = null;
        BufferedInputStream bis = null;
        try {
          bas = new ByteArrayOutputStream();
          bis = new BufferedInputStream(jfo.openInputStream());

          byte[] buf = new byte[1024];

          int read = -1;
          while ((read = bis.read(buf)) > -1) {
            bas.write(buf, 0, read);
          }
          byte[] array = bas.toByteArray();
          return defineClass(name, array, 0, array.length);
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
        finally {
          try {bis.close();} catch (Exception ignored) {}
          try {bas.close();} catch (Exception ignored) {}
        }
      }
      return super.findClass(name);
    }
  }

}
