package javassist;

import sample.SampleClass;

public class UpdateFieldAndMethodModifiers {

  public void addSynchronizedMakePublic() throws Exception {
    CtField field = JavassistHelper.getField(SampleClass.class, "instanceVariable");
    int fieldModifiers = field.getModifiers();
    field.setModifiers(Modifier.setPublic(fieldModifiers));
    
    CtMethod method = JavassistHelper.getMethod(SampleClass.class, "instanceMethod", String.class);
    int methodModifiers = field.getModifiers();
    method.setModifiers(methodModifiers | Modifier.SYNCHRONIZED);
  }
  
}
