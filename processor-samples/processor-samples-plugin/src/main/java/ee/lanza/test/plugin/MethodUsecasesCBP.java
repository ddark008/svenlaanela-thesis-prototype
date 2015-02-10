package ee.lanza.test.plugin;

import org.zeroturnaround.javassist.annotation.Patches;

import ee.lanza.javassist.annotation.processor.usecases.MethodUsecases;
import ee.lanza.javassist.annotation.processor.usecases.MethodUsecases_Mirror;

@Patches(MethodUsecases.class)
public class MethodUsecasesCBP extends MethodUsecases_Mirror {

	public static String publicStaticMethod(String $1) {
		System.out.println("public static field value before update: " + MethodUsecases_Mirror.publicStaticField);
		MethodUsecases_Mirror.publicStaticField = "updatedPublicStaticField";
		// TODO updating fields from companion does not work yet!
		System.out.println("public static field value after update: " + MethodUsecases_Mirror.publicStaticField);
		
		System.out.println("Before publicStaticMethod");
		String s = MethodUsecases_Mirror.publicStaticMethod($1);
		System.out.println("After publicStaticMethod");
		
		return s + "?";
	}
	
	public String publicMethod(String $1) {
		System.out.println("public field value before update: " + super.publicField);
		super.publicField = "updatedPublicField";
		// TODO updating fields from companion does not work yet!
		System.out.println("public field value after update: " + super.publicField);
		
		System.out.println("Before publicMethod");
		String s = super.publicMethod($1);
		System.out.println("After publicMethod");
		return s + "!";
	}
	
}
