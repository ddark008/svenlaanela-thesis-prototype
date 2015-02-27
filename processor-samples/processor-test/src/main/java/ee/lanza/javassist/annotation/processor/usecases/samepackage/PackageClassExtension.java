package ee.lanza.javassist.annotation.processor.usecases.samepackage;

import org.zeroturnaround.javassist.annotation.Patches;

import ee.lanza.javassist.annotation.processor.usecases.samepackage.PackageClass;

@Patches(PackageClass.class)
public class PackageClassExtension extends PackageClass_Mirror {

	public static String otherClassPublicStaticMethod(String $1) {
		$1 += OtherClass.publicStaticField + OtherClass.publicStaticMethod($1); 
		return PackageClass_Mirror.otherClassPublicStaticMethod($1);
	}
	
	public static String otherClassProtectedStaticMethod(String $1) {
		$1 += OtherClass.protectedStaticField + OtherClass.protectedStaticMethod($1); 
		return PackageClass_Mirror.otherClassProtectedStaticMethod($1);
	}
	
	public static String otherClassPackageStaticMethod(String $1) {
		$1 += OtherClass.packageStaticField + OtherClass.packageStaticMethod($1); 
		return PackageClass_Mirror.otherClassPackageStaticMethod($1);
	}
	
	public String otherClassPublicMethod(String $1) {
		$1 += otherClass.publicField + otherClass.publicMethod($1);
		return super.otherClassPublicMethod($1);
	}

	public String otherClassProtectedMethod(String $1) {
		$1 += otherClass.protectedField + otherClass.protectedMethod($1);
		return super.otherClassProtectedMethod($1);
	}

	public String otherClassPackageMethod(String $1) {
		$1 += otherClass.packageField + otherClass.packageMethod($1);
		return super.otherClassPackageMethod($1);
	}
	
	

}
