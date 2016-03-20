package ee.lanza.javassist.annotation.processor.usecases;

import java.util.HashMap;
import java.util.Map;

import org.zeroturnaround.javassist.annotation.processor.wiring.JavassistClassBytecodeProcessor;

import javassist.*;

/**
 * NotThreadSafe
 *
 */
public class TestUtil {

  private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

  /**
   * Gets the class specified by classname and patched by a processor and loads it under an
   * isolated classloader.
   */
  public static Class<?> getClass(String className, ClassLoader classLoader, JavassistClassBytecodeProcessor processor) {
    if (!classMap.containsKey(className)) {
      try {
        ClassPool classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        classPool.insertClassPath(new ClassClassPath(TestUtil.class));
        classPool.appendSystemPath();

        CtClass ctClass = classPool.get(className);
        processor.process(classPool, classLoader, ctClass);
        byte[] bytes = ctClass.toBytecode();

        ClassPool cp2 = ClassPool.getDefault();
        cp2.insertClassPath(new ByteArrayClassPath(className, bytes));
        CtClass cc = cp2.get(className);

        classMap.put(className, cc.toClass(classLoader, null));
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return classMap.get(className);
  }

  public static Object createInstance(String className) throws Exception {
    return Class.forName(className).newInstance();
  }
  
//  public static Object createInstance(String className, String processorName) throws Exception {
//    return createInstance(className, (JavassistClassBytecodeProcessor) Class.forName(processorName).newInstance());
//  }

  public static Object createInstance(ClassLoader classLoader, String className, String processorName) throws Exception {
    return getClass(className, classLoader, (JavassistClassBytecodeProcessor) classLoader.loadClass(processorName).newInstance()).newInstance();
  }


}
