package ee.lanza.javassist.annotation.processor.usecases;

/**
 * Different use-cases revolving around methods.
 *
 */
public class MethodUsecases {
	
	public final String publicMethod(String input) {
		return privateMethod(input);
	}
	
	private final String privateMethod(String input) {
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
