//package ee.lanza.javassist.annotation.processor.usecases;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javassist.ByteArrayClassPath;
//import javassist.ClassClassPath;
//import javassist.ClassPool;
//import javassist.CtClass;
//
//import org.zeroturnaround.javassist.annotation.processor.wiring.JavassistClassBytecodeProcessor;
//
///**
// * NotThreadSafe
// *
// */
//public class TestUtil {
//	
//	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
//	
//	public static Class<?> getClass(String className, JavassistClassBytecodeProcessor processor) {
//		if (!classMap.containsKey(className)) {
//			try {
//				ClassPool classPool = ClassPool.getDefault();
//				classPool.insertClassPath(new ClassClassPath(TestUtil.class));
//				CtClass ctClass = classPool.get(className);
//				processor.process(classPool, TestUtil.class.getClassLoader(), ctClass);
//				
//				byte[] bytes = ctClass.toBytecode();
//				
//				ClassPool cp2 = ClassPool.getDefault();
//				cp2.insertClassPath(new ByteArrayClassPath(className, bytes));
//				CtClass cc = cp2.get(className);
//				
//				classMap.put(className, cc.toClass());
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return classMap.get(className);
//	}
//	
//	public static Object createInstance(String className) throws Exception {
//	  return Class.forName(className).newInstance();
//	}
//	
//	public static Object createInstance(String className, JavassistClassBytecodeProcessor processor) throws Exception {
//		return getClass(className, processor).newInstance();
//	}
//
//}
