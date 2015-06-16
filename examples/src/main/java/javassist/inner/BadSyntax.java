package javassist.inner;

import javassist.CtMethod;
import sample.SampleClass;

public class BadSyntax {
  public void replaceMethodBody() throws Exception {
ClassPool cp = new ClassPool();
CtClass ctClass = cp.get(SampleClass.class.getName());
CtClass argClass = cp.get(String.class.getName());
CtMethod ctMethod = ctClass
  .getDeclaredMethod("gree", argClass);
ctMethod.insertBefore(
  "if (name == null) {"+
  "  return null"+
  "}");
  }
}

class ClassPool extends javassist.ClassPool {
  public CtClass get(String name) {
    return null;
  }
}

class CtClass extends javassist.CtClass {

  protected CtClass(String name) {
    super(name);
    // TODO Auto-generated constructor stub
  }
  
  public CtMethod getDeclaredMethod(String name, CtClass clazz) {
    return null;
  }
  
}