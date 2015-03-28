package javassist;

import sample.SampleClass;

public class ModifyMethod {
  
  public void addInsertBefore() throws Exception {
    ClassPool cp = new ClassPool();
    CtClass ctClass = cp.get(SampleClass.class.getName());
    CtClass[] params = new CtClass[] { cp.get(String.class.getName())};
    CtMethod ctMethod = ctClass.getDeclaredMethod("instanceMethod", params);
    ctMethod.insertBefore("instanceVariable = instanceVariable + \"Suffix\";");
  }
  
  public void addInsertAfterOverrideReturn() throws Exception {
    ClassPool cp = new ClassPool();
    CtClass ctClass = cp.get(SampleClass.class.getName());
    CtClass[] params = new CtClass[] { cp.get(String.class.getName())};
    CtMethod ctMethod = ctClass.getDeclaredMethod("instanceMethod", params);
    ctMethod.insertAfter("$_ = $_ + \"Suffix\";");
  }
  
  public void setBodyAccessParameters() throws Exception {
    ClassPool cp = new ClassPool();
    CtClass ctClass = cp.get(SampleClass.class.getName());
    CtClass[] params = new CtClass[] { cp.get(String.class.getName())};
    CtMethod ctMethod = ctClass.getDeclaredMethod("instanceMethod", params);
    ctMethod.setBody("return $1 + $1;");
  }

}
