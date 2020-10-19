package org.zeroturnaround.javassist.annotation.processor.wiring;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.zeroturnaround.javassist.annotation.processor.model.OriginalClass;
import org.zeroturnaround.javassist.annotation.processor.util.IOUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * The wiring class holds the logic that binds together the original class and 
 * its extension class.
 * 
 * 
 * @author lanza
 *
 */
public class WiringClassGenerator {
  private static final String WIRING_CLASS_SUFFIX = "CBP";
  
  private String originalClassName;
  private String extensionClassName;
  private ClassPool classPool;
  
  public WiringClassGenerator(ClassPool classPool, String originalClassName, String extensionClassName) {
    this.originalClassName = originalClassName;
    this.extensionClassName = extensionClassName;
    this.classPool = classPool;
  }
  
  public String getName() {
    return originalClassName + WIRING_CLASS_SUFFIX;
  }
  
  public String generateSource() {
    
    
    PrintWriter w = null;
    try {
      final CtClass original = classPool.get(originalClassName);
      
      OriginalClass oc = new OriginalClass();
      oc.packageName = original.getPackageName();
      oc.name = original.getName();
      oc.nameJVM = Descriptor.toJvmName(original);
      oc.cbpName = original.getName() + "CBP";
      oc.cbpSimpleName = original.getSimpleName() + "CBP";
      oc.extension.name = extensionClassName;
  
      Velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
      Velocity.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
  
      Context ctx = new VelocityContext();
      ctx.put("original", oc);
      ctx.put("extension", oc.extension);
      
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
