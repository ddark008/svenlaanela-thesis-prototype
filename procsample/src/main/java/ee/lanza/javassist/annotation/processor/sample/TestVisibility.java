package ee.lanza.javassist.annotation.processor.sample;

/**
 * Class for testing different types of visibility. The companion class should be able to access all the fields and methods
 * defined in the patched class as well as any non-private methods and fields defined on the patched class's superclass
 * 
 * Based on this current prototype, we can assume that having the companion class as an inner class works! :)
 * 
 * @author lanza
 *
 */
class Parent {
	
	public static String publicStaticParentField;
	static String packageStaticParentField;
	protected static String protectedStaticParentField;
	private static String privateStaticParentField;
	
	public static void publicStaticParentMethod() {};
	static void packageStaticParentMethod() {};
	protected static void protectedStaticParentMethod() {};
	private static void privateStaticParentMethod() {};
	
	public String publicParentField;
	String packageParentField;
	protected String protectedParentField;
	private String privateParentField;
	
	public void publicParentMethod() {};
	void packageParentMethod() {};
	protected void protectedParentMethod() {};
	private void privateParentMethod() {};
}

public class TestVisibility extends Parent {
	
	public TestVisibility() {
		System.out.println("TestVisibility");
	}
	
	public static void main(String[] args) {
		new TestVisibility().privateMethod();
	}
	
	public static String publicStaticField;
	static String packageStaticField;
	protected static String protectedStaticField;
	private static String privateStaticField;
	
	public static void publicStaticMethod() {};
	static void packageStaticMethod() {};
	protected static void protectedStaticMethod() {};
	private static void privateStaticMethod() {};
	
	public String publicField;
	String packageField;
	protected String protectedField;
	private String privateField;
	
	public void publicMethod() {};
	void packageMethod() {};
	protected void protectedMethod() {};
	private void privateMethod() {
		System.out.println(publicStaticParentField);
		System.out.println(packageStaticParentField);
		System.out.println(protectedStaticParentField);
		//System.out.println(privateStaticParentField); // illegal!
		
		publicStaticParentMethod();
		packageStaticParentMethod();
		protectedStaticParentMethod();
//		privateStaticParentMethod(); // illegal!
		
		System.out.println(publicParentField);
		System.out.println(packageParentField);
		System.out.println(protectedParentField);
//		System.out.println(privateParentField); // illegal!
		
		publicParentMethod();
		packageParentMethod();
		protectedParentMethod();
//		privateParentMethod(); // illegal!
		
		System.out.println(publicStaticField);
		System.out.println(packageStaticField);
		System.out.println(protectedStaticField);
		System.out.println(privateStaticField);
		
		publicStaticMethod();
		packageStaticMethod();
		protectedStaticMethod();
		privateStaticMethod();
		
		System.out.println(publicField);
		System.out.println(packageField);
		System.out.println(protectedField);
		System.out.println(privateField);
		
		publicMethod();
		packageMethod();
		protectedMethod();
		privateMethod();
		
		companion.privateMethod();
	};
	
	private Companion companion = new Companion();
	
	// AClickServletCBP
	private class Companion {
		
		public Companion() {
			System.out.println("Companion");
		}
		
		private String barString;
		
		private void privateMethod() {
			System.out.println(publicStaticParentField);
			System.out.println(packageStaticParentField);
			System.out.println(protectedStaticParentField);
			//System.out.println(privateStaticParentField); // illegal!
			
			publicStaticParentMethod();
			packageStaticParentMethod();
			protectedStaticParentMethod();
//			privateStaticParentMethod(); // illegal!
			
			System.out.println(publicParentField);
			System.out.println(packageParentField);
			System.out.println(protectedParentField);
//			System.out.println(privateParentField); // illegal!
			
			publicParentMethod();
			packageParentMethod();
			protectedParentMethod();
//			privateParentMethod(); // illegal!
			
			System.out.println(publicStaticField);
			System.out.println(packageStaticField);
			System.out.println(protectedStaticField);
			System.out.println(privateStaticField);
			
			publicStaticMethod();
			packageStaticMethod();
			protectedStaticMethod();
			privateStaticMethod();
			
			System.out.println(publicField);
			System.out.println(packageField);
			System.out.println(protectedField);
			System.out.println(privateField);
			
			publicMethod();
			packageMethod();
			protectedMethod();
			TestVisibility.this.privateMethod();
		}
		
	}

}
