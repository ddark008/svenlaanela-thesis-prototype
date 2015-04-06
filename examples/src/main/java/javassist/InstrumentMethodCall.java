package javassist;

import sample.InstrumentClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class InstrumentMethodCall {
  
  public void instrumentMethodCall() throws Exception {
    CtMethod method = JavassistHelper.getMethod(InstrumentClass.class, "instrumentMethod");
    method.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("method1".equals(m.getMethodName())) {
          m.replace("$_ = method2($1);"); 
        }
      }
    });
  }

}
