package com.zeroturnaround.jrebel.plugins.thymeleaf;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/**
 * Transforms the class <code>org.thymeleaf.TemplateEngine</code>. Sets empty body for the setCacheManager method. Current version doesn't modify the process() method, but there is
 * a {@link #patchProcessMethod(CtClass, ClassPool)} method for doing that.
 */
public abstract class DisableTemplateEngineCacheCBP extends JavassistClassBytecodeProcessor {

  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {

    try {
      patchSetCacheManagerMethod(ctClass, cp);
      // patchProcessMethod(ctClass, cp);
    }
    catch (NotFoundException e) {
    }
  }

  /**
   * Patches org.thymeleaf.TemplateEngine.setCacheManager(ICacheManager) method. Is is sufficient because there's
   * no other possibility to set Cache Managers for TemplateEngine.
   *
   * @param ctClass
   * @param cp
   * @throws NotFoundException
   * @throws CannotCompileException
   */
  private void patchSetCacheManagerMethod(CtClass ctClass, ClassPool cp) throws NotFoundException, CannotCompileException {
    CtClass cacheManagerClass = cp.get("org.thymeleaf.cache.ICacheManager");
    CtMethod setCacheManagerMethod = ctClass.getDeclaredMethod("setCacheManager", new CtClass[] { cacheManagerClass });
    setCacheManagerMethod.setBody("{}");

  }

  /**
   * Patches org.thymeleaf.TemplateEngine.process(TemplateProcessingParameters, IFragmentSpec, Writer) method by adding code that invokes
   * {@link TemplateEngineCacheInvalidator#clearAllCaches(org.thymeleaf.TemplateEngine)} in the beginning of this method.
   *
   * @param ctClass
   * @param cp
   * @throws NotFoundException
   * @throws CannotCompileException
   */
//  private void patchProcessMethod(CtClass ctClass, ClassPool cp) throws NotFoundException, CannotCompileException {
//    CtClass templateProcessingParametersClass = cp.get("org.thymeleaf.TemplateProcessingParameters");
//    CtClass fragmentSpecClass = cp.get("org.thymeleaf.fragment.IFragmentSpec");
//    CtClass writerClass = cp.get("java.io.Writer");
//
//    final String inject = "{" +
//        "org.zeroturnaround.jrebel.thymeleaf.TemplateEngineCacheInvalidator.clearAllCaches($0);" +
//        "}";
//    CtMethod processMethod = ctClass.getDeclaredMethod("process", new CtClass[] { templateProcessingParametersClass, fragmentSpecClass, writerClass });
//    processMethod.insertBefore(inject);
//  }

}
