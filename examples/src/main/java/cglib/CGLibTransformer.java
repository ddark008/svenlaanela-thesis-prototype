package cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.junit.Assert;
import org.junit.Test;

import sample.SampleClass;

public class CGLibTransformer {
  @Test
  public void testReplaceHelloWorld() throws Exception {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(SampleClass.class);
    enhancer.setCallback(new MethodInterceptor() {
      @Override
      public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if ("publicMethod".equals(method.getName())) {
          return "Hello world!";
        } else {
          return proxy.invokeSuper(target, args);
        }
      }
    });
    SampleClass proxy = (SampleClass) enhancer.create();
    Assert.assertEquals("Hello world!", proxy.publicMethod("Test"));
  }
}
