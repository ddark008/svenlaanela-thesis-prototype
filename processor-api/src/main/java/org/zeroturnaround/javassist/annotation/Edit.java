package org.zeroturnaround.javassist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Edit {
	public Class<?> type();
	public Class<?> returnType();
	public String method();
}
