package ee.lanza.javassist.annotation.processor.usecases.samepackage;

public class PackageClass {
	
	protected OtherClass otherClass = new OtherClass();
	
	/* static methods */
	public static final String otherClassPublicStaticMethod(String input) {
		return OtherClass.publicStaticField + OtherClass.publicStaticMethod(input);
	}
	
	public static final String otherClassProtectedStaticMethod(String input) {
		return OtherClass.protectedStaticField + OtherClass.protectedStaticMethod(input);
	}
	
	public static final String otherClassPackageStaticMethod(String input) {
		return OtherClass.packageStaticField + OtherClass.packageStaticMethod(input);
	}
	
	/* Illegal access */
//	public static final String otherClassPrivateStaticMethod(String input) {
//		return OtherClass.privateStaticField + OtherClass.privateStaticMethod(input);
//	}
	
	public final String otherClassPublicMethod(String input) {
		return otherClass.publicField + otherClass.publicMethod(input);
	}
	
	public final String otherClassProtectedMethod(String input) {
		return otherClass.protectedField + otherClass.protectedMethod(input);
	}
	
	public final String otherClassPackageMethod(String input) {
		return otherClass.packageField + otherClass.packageMethod(input);
	}
	
	/* Illegal access */
//	public final String otherClassPrivateMethod(String input) {
//		return otherClass.privateField + otherClass.privateMethod(input);
//	}

}
