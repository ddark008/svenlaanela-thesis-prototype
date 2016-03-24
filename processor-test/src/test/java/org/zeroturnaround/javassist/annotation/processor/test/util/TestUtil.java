package org.zeroturnaround.javassist.annotation.processor.test.util;

import org.zeroturnaround.javassist.annotation.processor.wiring.JavassistClassBytecodeProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class TestUtil {

  private static Map<String, Class<?>> classMap = new ConcurrentHashMap<String, Class<?>>();

  public static Object createTransformedClassInstance(String className, String processorName, ClassLoader classLoader) throws Exception {
    return createTransformedClass(className, processorName, classLoader).newInstance();
  }

  public static Class<?> createTransformedClass(String className, String processorName, ClassLoader classLoader) throws Exception {
    JavassistClassBytecodeProcessor processor = (JavassistClassBytecodeProcessor) classLoader.loadClass(processorName).newInstance();
    return createPatchedClass(className, processor, classLoader);
  }

  /**
   * Get the patched class specified by classname and patched by a processor and loads it under an
   * isolated classloader.
   */
  private static Class<?> createPatchedClass(String className, JavassistClassBytecodeProcessor processor, ClassLoader classLoader) {
    if (!classMap.containsKey(className)) {
      try {
        ClassPool classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        classPool.insertClassPath(new ClassClassPath(TestUtil.class));
        classPool.appendSystemPath();

        CtClass ctClass = classPool.get(className);
        processor.process(classPool, classLoader, ctClass);
        byte[] bytes = ctClass.toBytecode();

        Class<?> patchedClass = defineClass(className, bytes, classLoader);
        classMap.put(className, patchedClass);
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return classMap.get(className);
  }

  /**
   * Defines a new class under a specific classloader
   * @param name - the name of the class to be defined
   * @param bytes - the class bytecode
   * @param loader - the loader under which to define the class.
   */
  private static Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) throws NotFoundException, CannotCompileException {
    ClassPool classPool = new ClassPool();
//    classPool.insertClassPath(new LoaderClassPath(loader)); // TODO: are there some classes generated that the original class will need from transformation?
    classPool.insertClassPath(new ByteArrayClassPath(name, bytes));
    classPool.appendSystemPath();
    CtClass clazz = classPool.get(name);

    return clazz.toClass(loader, null);
  }

}
