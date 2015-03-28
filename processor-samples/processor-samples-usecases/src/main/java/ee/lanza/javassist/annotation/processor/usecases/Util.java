package ee.lanza.javassist.annotation.processor.usecases;

public class Util {
	public static final String reverse(String input) {
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
