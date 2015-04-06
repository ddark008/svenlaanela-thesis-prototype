package javassist;

import sample.InstrumentClass;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class InstrumentFieldAccess {
  
  public void instrumentFieldAccess() throws Exception {
    CtMethod method = JavassistHelper.getMethod(InstrumentClass.class, "instrumentMethod");
    method.instrument(new ExprEditor() {
      public void edit(FieldAccess f) throws CannotCompileException {
        if ("field1".equals(f.getFieldName())) {
          f.replace(
              "String s = $proceed();"+
              "if (s == null) {"+
              "  $_ = \"\";"+
              "} else {"+
              "  $_ = s;"+
              "}"); 
        }
      }
    });
  }

}
