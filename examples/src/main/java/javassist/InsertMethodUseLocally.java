package javassist;

import sample.SampleClass;

public class InsertMethodUseLocally {

  public void addTrimMethodAndUseLocally() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    ctClass.addMethod(CtNewMethod.make(
        "public String trim(String input) {"+
        "  if (input == null) {" +
        "    return null;"+
        "  }"+
        "  return input.trim();"+
        "}", ctClass));
    
    CtMethod ctMethod = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    ctMethod.insertBefore("$1 = trim($1);");
  }
  
}
