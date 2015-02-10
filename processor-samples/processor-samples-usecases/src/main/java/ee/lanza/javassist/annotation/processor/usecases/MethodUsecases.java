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
		return publicStaticField + reverse(input);
	}
	
	public final String publicMethod(String input) {
		return publicField + reverse(input);
	}
	
	private final String privateMethod(String input) {
		return privateField + reverse(input);
	}
	
	private final String privateStaticMethod(String input) {
		return privateStaticField + reverse(input);
	}
	
	private static final String reverse(String input) {
		if (input == null) {
			return null;
		}
		
		String s = "";
		for (int i = input.length() - 1; i >=0 ; i--) {
			s += input.charAt(i);
		}
		return s;
	}

}
