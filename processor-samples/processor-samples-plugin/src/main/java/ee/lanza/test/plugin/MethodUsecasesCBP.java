package ee.lanza.test.plugin;

import org.zeroturnaround.javassist.annotation.Patches;

import ee.lanza.javassist.annotation.processor.usecases.MethodUsecases;
import ee.lanza.javassist.annotation.processor.usecases.MethodUsecases_Mirror;

@Patches(MethodUsecases.class)
public class MethodUsecasesCBP extends MethodUsecases_Mirror {

	public static String publicStaticMethod(String $1) {
		System.out.println("Before publicStaticMethod");
		String s = MethodUsecases_Mirror.publicStaticMethod($1);
		System.out.println("After publicStaticMethod");
		return s + "?";
	}
	
	public String publicMethod(String $1) {
		System.out.println("Before publicMethod");
		String s = super.publicMethod($1);
		System.out.println("After publicMethod");
		return s + "!";
	}
	
}
