package ee.lanza.javassist.annotation.processor.usecases.samepackage;

import ee.lanza.javassist.annotation.processor.usecases.Util;

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
		return publicStaticField + Util.reverse(input);
	}
	
	protected static final String protectedStaticMethod(String input) {
		return protectedStaticField + Util.reverse(input);
	}
	
	static final String packageStaticMethod(String input) {
		return packageStaticField + Util.reverse(input);
	}
	
	private static final String privateStaticMethod(String input) {
		return privateStaticField + Util.reverse(input);
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
		return publicField + Util.reverse(input);
	}
	
	protected final String protectedMethod(String input) {
		return protectedField + Util.reverse(input);
	}
	
	final String packageMethod(String input) {
		return packageField + Util.reverse(input);
	}
	
	private final String privateMethod(String input) {
		return privateField + Util.reverse(input);
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

}
