package ee.lanza.javassist.annotation.processor.usecases.samepackage;

public class OtherClass {
	
	public static final String publicStaticField = "publicStaticField";
	protected static final String protectedStaticField = "protectedStaticField";
	final static String packageStaticField = "packageStaticField";
	private final static String privateStaticField = "privateStaticField";

	public final String publicField = "publicField";
	protected final String protectedField = "protectedField";
	final String packageField = "packageField";
	private final String privateField = "privateField";
	
	
	
	/* static methods */
	public static final String publicStaticMethod(String input) {
		return publicStaticField + reverse(input);
	}
	
	protected static final String protectedStaticMethod(String input) {
		return protectedStaticField + reverse(input);
	}
	
	static final String packageStaticMethod(String input) {
		return packageStaticField + reverse(input);
	}
	
	private static final String privateStaticMethod(String input) {
		return privateStaticField + reverse(input);
	}
	
	/* static accessors */
	public static String publicStaticMethodAccessor(String input) {
		return publicStaticMethod(input);
	}
	
	public static String protectedStaticMethodAccessor(String input) {
		return protectedStaticMethod(input);
	}
	
	public static String packageStaticMethodAccessor(String input) {
		return packageStaticMethod(input);
	}
	
	public static String privateStaticMethodAccessor(String input) {
		return privateStaticMethod(input);
	}
	
	
	
	
	/* instance methods */
	public final String publicMethod(String input) {
		return publicField + reverse(input);
	}
	
	protected final String protectedMethod(String input) {
		return protectedField + reverse(input);
	}
	
	final String packageMethod(String input) {
		return packageField + reverse(input);
	}
	
	private final String privateMethod(String input) {
		return privateField + reverse(input);
	}
	
	/* instance accessors */
	public String publicMethodAccessor(String input) {
		return publicMethod(input);
	}
	
	public String protectedMethodAccessor(String input) {
		return protectedMethod(input);
	}
	
	public String packageMethodAccessor(String input) {
		return packageMethod(input);
	}
	
	public String privateMethodAccessor(String input) {
		return privateMethod(input);
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
