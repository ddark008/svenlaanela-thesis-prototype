package javassist;

import sample.OtherClass;
import sample.SampleClass;

public class InsertMethodUseExternally {
  
  public void addTrimMethodAndUseExternally() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    ctClass.addMethod(CtNewMethod.make(
        "public String trim(String input) {"+
        "  if (input == null) {" +
        "    return null;"+
        "  }"+
        "  return input.trim();"+
        "}", ctClass));
    ctClass.addInterface(JavassistHelper.getClass(Trimmable.class));
    
    CtMethod ctMethod = JavassistHelper.getMethod(OtherClass.class, "otherMethod", String.class);
    ctMethod.insertBefore("$1 = (("+Trimmable.class.getName()+") new SampleClass()).trim($1);");
  }

}
