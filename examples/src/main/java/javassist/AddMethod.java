package javassist;

import sample.SampleClass;
import sample.other.PublicClass;

public class AddMethod {

  public void addMethodUseLocally() throws Exception {
    ClassPool cp = new ClassPool();
    CtClass ctClass = cp.get(SampleClass.class.getName());
    ctClass.addMethod(CtNewMethod.make(
        "public String trim(String value) {"+
        "  if (value == null) return null;"+
        "  value.trim();"+
        "}", ctClass));
    CtClass[] params = new CtClass[] { cp.get(String.class.getName())};
    CtMethod ctMethod = ctClass.getDeclaredMethod("instanceMethod", params);
    ctMethod.insertBefore("$1 = trim($1);");
  }
  
  public void addMethodUseExternally() throws Exception {
    ClassPool cp = new ClassPool();
    CtClass ctClass = cp.get(SampleClass.class.getName());
    ctClass.addInterface(cp.get(Trimmable.class.getName()));
    ctClass.addMethod(CtNewMethod.make(
        "public String trim(String value) {"+
        "  if (value == null) return null;"+
        "  value.trim();"+
        "}", ctClass));
    
    CtClass ctPublicClass = cp.get(PublicClass.class.getName());
    CtClass[] params = new CtClass[] { cp.get(String.class.getName())};
    CtMethod ctPublicMethod = ctPublicClass.getDeclaredMethod("publicInstanceMethod", params);
    String trimmable = Trimmable.class.getName();
    ctPublicMethod.insertBefore("$1 = (("+trimmable+") new SampleClass()).trim($1);");
  }
  
  public static interface Trimmable {
    public String trim(String value);
  }
  
}
