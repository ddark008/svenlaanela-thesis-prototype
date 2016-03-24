package org.zeroturnaround.javassist.annotation.processor.test.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

public class AdHocCompilationResultsClassLoader extends URLClassLoader {
  private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();
  private final Map<String, JavaFileObject> resources = new HashMap<String, JavaFileObject>();

  public AdHocCompilationResultsClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public void appendToClassPath(Map<String, JavaFileObject> javaFileObjects) {
    for (Map.Entry<String, JavaFileObject> jfo : javaFileObjects.entrySet()) {
      String name = jfo.getKey();
      resources.put(name, jfo.getValue());

      name = name.replace("/", ".");
      if (name.endsWith(".class")) {
        name = name.substring(0, name.length()-6);
      }
      classes.put(name, jfo.getValue());
    }
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
      InputStream jfoInputStream = null;
      try {
        jfoInputStream = jfo.openInputStream();
        byte[] bytes = IOUtils.toByteArray(jfoInputStream);
        return defineClass(name, bytes, 0, bytes.length);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
      finally {
        IOUtils.closeQuietly(jfoInputStream);
      }
    }
    return super.findClass(name);
  }
}