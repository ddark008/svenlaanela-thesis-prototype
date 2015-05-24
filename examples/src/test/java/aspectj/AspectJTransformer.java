package aspectj;

import java.io.ByteArrayInputStream;
import java.net.URLClassLoader;

import javassist.ClassPool;

import org.apache.commons.io.IOUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.aspectj.weaver.tools.WeavingAdaptor;
import org.junit.Assert;
import org.junit.Test;

import sample.SampleClass;

@Aspect
class ReplacingAspect {
  @Around("execution(private * instanceMethod(..))")
  public String publicMethod(final ProceedingJoinPoint pjp) {
    return "Hello world!";
  }
}

public class AspectJTransformer {
  @Test
  public void testReplaceMethodBody() throws Exception {
    Object o = toClass();
    Assert.assertEquals("Hello world", ((SampleClass) o).publicMethod("Test"));
  }
  
  private Object toClass() throws Exception {
    URLClassLoader parentLoader = ((URLClassLoader)this.getClass().getClassLoader());
    WeavingURLClassLoader cl = new WeavingURLClassLoader(parentLoader.getURLs(), parentLoader.getURLs(), parentLoader);
    WeavingAdaptor adaptor = new WeavingAdaptor(cl);
    
    byte[] modifiedBytes = adaptor.weaveClass("sample.SampleClass", IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("sample/SampleClass.class")), true);
    
    return cl.loadClass("sample.SampleClass").newInstance();
    
//    ClassPool cp = ClassPool.getDefault();
//    
//    return cp.makeClass(new ByteArrayInputStream(modifiedBytes)).toClass().newInstance();
  }
}
