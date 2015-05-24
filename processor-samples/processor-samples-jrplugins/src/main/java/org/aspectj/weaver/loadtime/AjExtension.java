package org.aspectj.weaver.loadtime;

import org.aspectj.weaver.tools.Trace;
import org.zeroturnaround.javassist.annotation.MethodCall;
import org.zeroturnaround.javassist.annotation.Patches;

/**
 * Add annotation to method that we want to edit.
 * During compile-time we check that this kind of method exists, if it does not, give compilation error
 * If it does exist, method mirrors are generated which can be overriden and with which you can return and call super to edit the call
 * 
 *
 */

interface VersionDetector {
	String detectVersion();
}

class AspectJVersionDetector implements VersionDetector {
	public String detectVersion() {
		return "1.0";
	}
}


@Patches(org.aspectj.weaver.loadtime.Aj.class)
//@Version(min = "1", max = "1.16", AspectJVersionDetector.class)
public class AjExtension extends Aj_Mirror {

	private Aj_Mirror original = new Aj_Mirror();
	
	@Override
//	@Edit(type=Trace.class, returnType=Void.class, method="error", id=1)
//	@Edit(type=Trace.class, returnType=Void.class, method="error", id=2)
//	@Edit(type=Trace.class, returnType=Void.class, method="error", id=3)
//	@Edit(type=Trace.class, returnType=Void.class, method="error", id=4)
	public byte[] preProcess(String $1, byte[] $2, ClassLoader $3) {
		System.out.println("");
		super.instrument(new MethodCall() {
			void error(Trace $0, String $1, int $2) {
				if (1==1)
					$0.error($1);
			}
		});
		return original.preProcess($1, $2, $3);
	}
	
//	@Id(1)
//	public void error(Trace $0, String $1, int $2) {
//		if(1==1)
//			$0.error($1);
//	
//		
//	}
}