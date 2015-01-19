package com.zeroturnaround.jrebel.plugins.click;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;


/**
 * @author sander at zeroturnaround dot com
 */
public abstract class MenuFactoryCBP extends JavassistClassBytecodeProcessor {

  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.jrebel.integration.click");
    cp.importPackage("org.apache.click");
    cp.importPackage("org.apache.click.extras.control");
    cp.importPackage("java.util");
    cp.importPackage("javax.servlet");
    
    CtClass[] params = cp.get(new String[] {
        "java.lang.String",
        "java.lang.String",
        "org.apache.click.extras.security.AccessController",
        "boolean",
        "java.lang.Class"
    });
    
    CtMethod m = ctClass.getDeclaredMethod("getRootMenu", params);
    
    m.insertBefore(
        "ServletContext servletContext = Context.getThreadLocalContext().getServletContext(); " +
        "ClassLoader classLoader = MenuFactory.class.getClassLoader(); " +
        "Class factoryClass = MenuFactory.class; " +
        
        "if (ClickHelper.needsRefresh(servletContext, classLoader, factoryClass, $2)) {" +
            // invalidate the old menu entry
        "  if (menuCache != null) { " +
        "    menuCache.put($1, null);" +
        "  }" +
        "}" +
  
        "menuCache = new java.util.HashMap();"
    );
    
  }
}

