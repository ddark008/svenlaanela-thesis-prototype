package org.zeroturnaround.javassist.annotation.processor.model;

public class OriginalClass {
	
	public String packageName;
	public String cbpName;
	public String cbpSimpleName;
	public String name;
	public ExtensionClass extension = new ExtensionClass();
	
	/*
	 * Adding public getters because velocity is RETARDED.
	 */
	public String getPackageName() { return packageName; }
	public String getCbpName() { return cbpName; }
	public String getCbpSimpleName() { return cbpSimpleName; }
	public String getName() { return name; }
	

}
