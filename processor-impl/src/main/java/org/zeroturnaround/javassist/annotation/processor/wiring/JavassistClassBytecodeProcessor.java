package org.zeroturnaround.javassist.annotation.processor.wiring;

import javassist.ClassPool;
import javassist.CtClass;

public interface JavassistClassBytecodeProcessor {
	public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception;
}
