package ee.lanza.javassist.annotation.processor.sample;

import org.apache.click.ClickServlet;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import ee.lanza.javassist.annotation.processor.loader.JavassistCBPLoader;

public class Main {
	
	public static void main(String[] args) {
		try {
			byte[] bytes = new JavassistCBPLoader().process("org.apache.click.ClickServlet");
			
			ClassPool cp2 = ClassPool.getDefault();
			cp2.insertClassPath(new ByteArrayClassPath("org.apache.click.ClickServlet", bytes));
			CtClass cc = cp2.get("org.apache.click.ClickServlet");
			
			ClickServlet clickServlet = (ClickServlet) cc.toClass().newInstance();
			clickServlet.init(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
