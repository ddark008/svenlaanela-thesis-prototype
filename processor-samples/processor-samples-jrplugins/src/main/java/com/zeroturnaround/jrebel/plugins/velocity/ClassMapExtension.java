package com.zeroturnaround.jrebel.plugins.velocity;

import java.lang.reflect.Method;

import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.ClassMap;
import org.apache.velocity.util.introspection.ClassMap_Mirror;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.ReloaderFactory;
import org.zeroturnaround.javarebel.integration.util.WeakUtil;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(ClassMap.class)
public class ClassMapExtension extends ClassMap_Mirror implements ClassEventListener {

	//@Override / @Extend
//	private volatile MethodCache_Mirror methodCache;
	
	public ClassMapExtension(Class $1, Log $2) {
		super($1, $2);
		ReloaderFactory.getInstance().addHierarchyReloadListener(clazz, WeakUtil.weakCEL(this));
	}

	@Override
	public Method findMethod(String $1, Object[] $2) {
		ReloaderFactory.getInstance().checkAndReload(clazz);
		return super.findMethod($1, $2);
	}

	@Override
	public void onClassEvent(int arg0, Class<?> arg1) {
		methodCache = createMethodCache();
	}

	@Override
	public int priority() {
		return ClassEventListener.PRIORITY_DEFAULT;
	}

	
	
	
	

}
