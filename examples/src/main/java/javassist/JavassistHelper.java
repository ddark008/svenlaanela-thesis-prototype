package javassist;

import java.util.ArrayList;
import java.util.List;

public class JavassistHelper {
  public static CtClass getClass(Class<?> clazz) throws NotFoundException {
    return getClass(clazz.getName());
  }
  
  public static CtClass getClass(String className) throws NotFoundException {
    ClassPool classPool = ClassPool.getDefault();
    return classPool.get(className);
  }
  
  public static CtClass[] getClasses(String... classNames) throws NotFoundException {
    List<CtClass> result = new ArrayList<CtClass>();
    for (String className : classNames) {
      result.add(getClass(className));
    }
    return result.toArray(new CtClass[result.size()]);
  }
  
  public static CtMethod getMethod(Class<?> clazz, String methodName, Class<?>... argClasses) throws NotFoundException {
    return getMethod(clazz.getName(), methodName, toClassNames(argClasses));
  }
  
  public static CtMethod getMethod(String className, String methodName, String... argClassNames) throws NotFoundException {
    CtClass theClass = getClass(className);
    CtClass[] paramClasses = getClasses(argClassNames);
    return theClass.getDeclaredMethod(methodName, paramClasses);
  }
  
  public static CtConstructor getConstructor(String className, String... argClassNames) throws NotFoundException {
    CtClass theClass = getClass(className);
    CtClass[] paramClasses = getClasses(argClassNames);
    return theClass.getDeclaredConstructor(paramClasses);
  }
  
  public static CtConstructor getConstructor(Class<?> clazz, Class<?>... argClasses) throws NotFoundException {
    return getConstructor(clazz.getName(), toClassNames(argClasses));
  }
  
  public static CtField getField(Class<?> clazz, String fieldName) throws NotFoundException {
    return getField(clazz.getName(), fieldName);
  }
  
  public static CtField getField(String className, String fieldName) throws NotFoundException {
    CtClass theClass = getClass(className);
    return theClass.getField(fieldName);
  }
  
  private static String[] toClassNames(Class<?>...classes) {
    List<String> result = new ArrayList<String>();
    for (Class<?> clazz : classes) {
      result.add(clazz.getName());
    }
    return result.toArray(new String[result.size()]);
  }
}
