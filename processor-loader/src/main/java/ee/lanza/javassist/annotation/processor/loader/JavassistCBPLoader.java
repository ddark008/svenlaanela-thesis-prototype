package ee.lanza.javassist.annotation.processor.loader;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.zeroturnaround.javassist.annotation.processor.JavassistClassBytecodeProcessor;

// Uses the local classloader to lookup the class bytecode, and returns it by applying transformations
public class JavassistCBPLoader {
	
	private final ClassPool classPool;
	
	public JavassistCBPLoader() {
		classPool = ClassPool.getDefault();
		classPool.insertClassPath(new ClassClassPath(this.getClass()));
	}
	
	public byte[] process(String className) throws Exception {
		// scan for CBP-s from classpath/classloader that should apply to this class?
		JavassistClassBytecodeProcessor[] processors = doLookup(className);
		if (processors.length != 0) {
			try {
				CtClass ctClass = classPool.get(className);
				
				for (JavassistClassBytecodeProcessor processor : processors) {
					// does this apply multiple processors?
					processor.process(classPool, classPool.getClassLoader(), ctClass); 
				}
				return ctClass.toBytecode();
			} catch (NotFoundException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Class " + className + " not found in classloader " + classPool.getClassLoader());
			} catch (Exception e) {
				throw e;
			}
		}
		return null; // what if no processors were found?
	}

	public static void writeToFile(String name, byte[] bytes) throws FileNotFoundException,
			IOException {
		FileOutputStream stream = new FileOutputStream(name);
		try {
		    stream.write(bytes);
		} finally {
		    stream.close();
		}
	}
	
	// TODO: dynamic lookup mechanism
	private JavassistClassBytecodeProcessor[] doLookup(String className) {
		try {
			if ("org.apache.click.ClickServlet".equals(className)) {
				return new JavassistClassBytecodeProcessor[] { (JavassistClassBytecodeProcessor) Class.forName("org.apache.click.ClickServletCBP").newInstance()};
			} else {
				return new JavassistClassBytecodeProcessor[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load CBP class");
		}
	}

}
