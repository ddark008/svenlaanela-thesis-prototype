package ee.lanza.javassist.annotation.processor.sample;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import org.apache.click.ClickServlet;

import ee.lanza.javassist.annotation.processor.loader.JavassistCBPLoader;
import ee.lanza.javassist.annotation.processor.usecases.MethodUsecases;

public class Main {
	
	public static void main(String[] args) throws Exception {
		try {
			clickServletTest();
			methodUsecasesTest();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	private static void clickServletTest() throws Exception {
		byte[] bytes = new JavassistCBPLoader().process("org.apache.click.ClickServlet");
		
		ClassPool cp2 = ClassPool.getDefault();
		cp2.insertClassPath(new ByteArrayClassPath("org.apache.click.ClickServlet", bytes));
		CtClass cc = cp2.get("org.apache.click.ClickServlet");
		
		ClickServlet clickServlet = (ClickServlet) cc.toClass().newInstance();
		
		clickServlet.init();
	}
	
	private static void methodUsecasesTest() throws Exception {
		byte[] bytes = new JavassistCBPLoader().process("ee.lanza.javassist.annotation.processor.usecases.MethodUsecases");
		
		ClassPool cp2 = ClassPool.getDefault();
		cp2.insertClassPath(new ByteArrayClassPath("ee.lanza.javassist.annotation.processor.usecases.MethodUsecases", bytes));
		CtClass cc = cp2.get("ee.lanza.javassist.annotation.processor.usecases.MethodUsecases");
		
		MethodUsecases usecases = (MethodUsecases) cc.toClass().newInstance();
		
		System.out.println(usecases.publicMethod("instance"));
		System.out.println(usecases.publicStaticMethod("static"));
	}
}
