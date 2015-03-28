package ee.lanza.javassist.annotation.processor.usecases;

/**
 * Different use-cases revolving around methods.
 *
 */
public class MethodUsecases {
	
	private String privateField = "privateField";
	private static String privateStaticField = "privateStaticField";
	public String publicField = "publicField";
	public static String publicStaticField = "publicStaticField";
	
	public static final String publicStaticMethod(String input) {
		return publicStaticField + Util.reverse(input);
	}
	
	public final String publicMethod(String input) {
		return publicField + Util.reverse(input);
	}
	
	private final String privateMethod(String input) {
		return privateField + Util.reverse(input);
	}
	
	private final String privateStaticMethod(String input) {
		return privateStaticField + Util.reverse(input);
	}
	


}
