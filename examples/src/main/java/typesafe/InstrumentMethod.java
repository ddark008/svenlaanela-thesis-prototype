package typesafe;

import org.zeroturnaround.javassist.annotation.MethodCall;
import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

import sample.InstrumentClass;

@Patches(InstrumentClass.class)
public class InstrumentMethod extends InstrumentClass_Mirror {
  @Modify
  public String publicMethod(String input) {
    instrument(new MethodCall() {
      public String method1(InstrumentClass_Mirror $0, String $1) {
        return $0.method2($1);
      }
    });
    
    return super.instrumentMethod(input) + "!";
  }
}