package org.zeroturnaround.javassist.annotation.processor.wiring;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.zeroturnaround.javassist.annotation.processor.model.OriginalClass;
import org.zeroturnaround.javassist.annotation.processor.util.IOUtil;

public class WiringClass {
  private static final String WIRING_CLASS_SUFFIX = "CBP";
  
  private String originalClassName;
  private String extensionClassName;
  
  public WiringClass(String originalClassName, String extensionClassName) {
    this.originalClassName = originalClassName;
    this.extensionClassName = extensionClassName;
  }
  
  public String getName() {
    return originalClassName + WIRING_CLASS_SUFFIX;
  }
  
  public String generateSource() {
    PrintWriter w = null;
    try {
      ClassPool classPool = new ClassPool();
      classPool.insertClassPath(new ClassClassPath(this.getClass()));
      final CtClass original = classPool.get(originalClassName);
      
      OriginalClass oc = new OriginalClass();
      oc.packageName = original.getPackageName();
      oc.name = original.getName();
      oc.cbpName = original.getName() + "CBP";
      oc.cbpSimpleName = original.getSimpleName() + "CBP";
      oc.companion.name = extensionClassName;
  
      Velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
      Velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
  
      Context ctx = new VelocityContext();
      ctx.put("original", oc);
      ctx.put("companion", oc.companion);
      
      StringWriter s = new StringWriter();
      w = new PrintWriter(new BufferedWriter(s));
      Velocity.mergeTemplate("cbp.vtl", "utf-8", ctx, w);
      w.flush();
      
      return s.toString();
    } catch (NotFoundException e) {
      throw new IllegalArgumentException("Original class " + originalClassName + " not found!"); // should not happen
    } finally {
      IOUtil.closeQuietly(w);
    }
  }

}
