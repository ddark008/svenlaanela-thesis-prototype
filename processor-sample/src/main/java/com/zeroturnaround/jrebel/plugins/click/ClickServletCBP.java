package com.zeroturnaround.jrebel.plugins.click;

import javassist.*;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;


/**
 * @author sander at zeroturnaround dot com
 */
public abstract class ClickServletCBP extends JavassistClassBytecodeProcessor {

  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("javax.servlet");
    cp.importPackage("org.zeroturnaround.javarebel");
    
    addMethodRebuildConfigService(ctClass);
    patchHandleRequest(cp, ctClass);
    
  }

  private void addMethodRebuildConfigService(CtClass ctClass) throws CannotCompileException {
    ctClass.addMethod(
        CtNewMethod.make(
            " private void rebuildConfigService() { " +
                "   LoggerFactory.getLogger(\"Click\").echo(\"Rebuilding application configuration ..\"); " +
                "   ServletContext sc = getServletContext(); " +
                "   configService = createConfigService(sc); " +
                "   initConfigService(sc); " +
                " }",
            ctClass
        )
    );
  }

  private void patchHandleRequest(ClassPool cp, CtClass ctClass) throws NotFoundException, CannotCompileException {
    CtClass[] params = cp.get(new String[] {
        "javax.servlet.http.HttpServletRequest",
        "javax.servlet.http.HttpServletResponse",
        "boolean"
    });
    
    CtMethod m = ctClass.getDeclaredMethod("handleRequest", params);
    
    m.insertBefore(
      " rebuildConfigService();                                                                 "
    );
  }
}

