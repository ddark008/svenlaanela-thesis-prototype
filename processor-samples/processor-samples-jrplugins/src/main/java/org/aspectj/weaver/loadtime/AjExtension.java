package org.aspectj.weaver.loadtime;

import org.aspectj.weaver.tools.Trace;
import org.zeroturnaround.javassist.annotation.Edit;
import org.zeroturnaround.javassist.annotation.Patches;

/**
 * Add annotation to method that we want to edit.
 * During compile-time we check that this kind of method exists, if it does not, give compilation error
 * If it does exist, method mirrors are generated which can be overriden and with which you can return and call super to edit the call
 * 
 *
 */
@Patches(org.aspectj.weaver.loadtime.Aj.class)
public class AjExtension extends Aj_Mirror {

	@Override
//	@Edit(type=Trace.class, returnType=Void.class, method="error")
	public byte[] preProcess(String $1, byte[] $2, ClassLoader $3) {
		return super.preProcess($1, $2, $3);
	}
	
	

}