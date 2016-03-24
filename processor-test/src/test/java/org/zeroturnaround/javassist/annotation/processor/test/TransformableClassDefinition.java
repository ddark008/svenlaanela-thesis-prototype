package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.processor.test.util.AdHocCompilationResultsClassLoader;
import org.zeroturnaround.javassist.annotation.processor.test.util.TestUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

public class TransformableClassDefinition {
  public final String className;
  public final String extensionName;
  private final AdHocCompilationResultsClassLoader classLoader;
  private final Map<String, TransformedClassDefinition> transformedClasses = new HashMap<String, TransformedClassDefinition>();

  public TransformableClassDefinition(Class<?> clazz, String extensionSuffix) {
    this(clazz.getName(), extensionSuffix);
  }

  public TransformableClassDefinition(String className, String extensionSuffix) {
    this.className = className;
    this.extensionName = className + extensionSuffix;

    /**
     * The classloader mimics the AppClassLoader, but has additional stuff in its classpath which is generated ad-hoc during runtime.
     */
    ClassLoader classLoader = TransformableClassDefinition.class.getClassLoader();
    this.classLoader = new AdHocCompilationResultsClassLoader(((URLClassLoader) classLoader).getURLs(), classLoader.getParent());
  }

  public TransformedClassDefinition transform() throws Exception {
    return transform(classLoader);
  }

  public TransformedClassDefinition transform(String cbpName) throws Exception {
    return transform(classLoader, cbpName);
  }

  public TransformedClassDefinition transform(ClassLoader cl) throws Exception {
    return transform(cl, className + "CBP");
  }

  public TransformedClassDefinition transform(ClassLoader cl, String cbpName) throws Exception {
    if (!transformedClasses.containsKey(cbpName)) {
      /**
       * NB! Important!
       */
      Class<?> transformedClass = (Class<?>) cl.loadClass(TestUtil.class.getName()).getDeclaredMethod("createTransformedClass", String.class, String.class, ClassLoader.class).invoke(null, className, cbpName, cl);
      transformedClasses.put(cbpName, new TransformedClassDefinition(transformedClass));
    }
    return transformedClasses.get(cbpName);
  }

  public void appendToClassPath(Map<String, JavaFileObject> javaFileObjects) {
    classLoader.appendToClassPath(javaFileObjects);
  }

  public class TransformedClassDefinition {
    private final Class<?> transformedClass;

    public TransformedClassDefinition(Class<?> transformedClass) {
      this.transformedClass = transformedClass;
    }

    // TODO: support non-default constructors
    public TransformedClassInstance construct() throws Exception {
      Object instance = transformedClass.newInstance();
      return new TransformedClassInstance(instance);
    }

    public TransformedClassFieldAccessor field(String fieldName) throws Exception {
      return new TransformedClassFieldAccessor(transformedClass.getDeclaredField(fieldName), null);
    }

    public <T> TransformedClassFieldAccessor<T> field(String fieldName, Class<T> fieldType) throws Exception {
      return new TransformedClassFieldAccessor<T>(transformedClass.getDeclaredField(fieldName), null);
    }

    public TransformedClassMethodAccessor method(String methodName, Class<?>... paramTypes) throws Exception {
      return method(methodName, Object.class, paramTypes);
    }

    public <T> TransformedClassMethodAccessor<T> method(String methodName, Class<T> returnType, Class<?>... paramTypes) throws Exception {
      return new TransformedClassMethodAccessor<T>(transformedClass.getDeclaredMethod(methodName), null);
    }

    public class TransformedClassInstance {
      private final Object instance;
      public TransformedClassInstance(Object instance) {
        this.instance = instance;
      }

      public TransformedClassFieldAccessor field(String fieldName) throws NoSuchFieldException {
        return field(fieldName, Object.class);
      }

      public <T> TransformedClassFieldAccessor<T> field(String fieldName, Class<T> fieldType) throws NoSuchFieldException {
        return new TransformedClassFieldAccessor<T>(transformedClass.getDeclaredField(fieldName), instance);
      }

      public TransformedClassMethodAccessor method(String methodName, Class<?>... paramTypes) throws NoSuchMethodException {
        return method(methodName, Object.class, paramTypes);
      }

      public <T> TransformedClassMethodAccessor<T> method(String methodName, Class<T> returnType, Class<?>... paramTypes) throws NoSuchMethodException {
        return new TransformedClassMethodAccessor<T>(transformedClass.getDeclaredMethod(methodName, paramTypes), instance);
      }
    }

    public class TransformedClassFieldAccessor<T> {
      private final Object target;
      private final Field field;
      public TransformedClassFieldAccessor(Field field, Object target) {
        this.field = field;
        this.target = target;
      }

      public T access() throws IllegalAccessException {
        return (T) field.get(target);
      }
    }

    public class TransformedClassMethodAccessor<T> {
      private final Object target;
      private final Method method;
      public TransformedClassMethodAccessor(Method method, Object target) {
        this.method = method;
        this.target = target;
      }

      public T invoke(Object... params) throws InvocationTargetException, IllegalAccessException {
        return (T) method.invoke(target, params);
      }
    }
  }
}