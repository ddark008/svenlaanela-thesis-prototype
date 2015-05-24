package javassist;

import sample.SampleClass;

public class OverwriteField {
  public void overwriteField() throws Exception {
    CtConstructor[] constructors = JavassistHelper.getAllConstructors(SampleClass.class);
    for (CtConstructor constructor : constructors) {
      constructor.insertAfter("instanceField = \"newValue\";");
    }
  }
}
