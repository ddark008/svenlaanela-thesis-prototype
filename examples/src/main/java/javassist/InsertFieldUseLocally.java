package javassist;

import sample.SampleClass;

public class InsertFieldUseLocally {
  public void insertFieldUseLocally() throws Exception {
    CtClass ctClass = JavassistHelper.getClass(SampleClass.class);
    ctClass.addField(CtField.make(
        "private String addedField = \"addedField\";", 
        ctClass));
    
    CtMethod ctMethod = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    ctMethod.insertBefore("$1 = $1 + addedField;");
  }
}
