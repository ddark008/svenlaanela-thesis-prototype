package sample;

import org.zeroturnaround.javassist.annotation.MethodCall;

public class InstrumentClass_Mirror {
  public final void instrument(MethodCall call) {}
  
  public String method1(String $1) {
    return null;
  }
  
  public String method2(String $2) {
    return null;
  }
  
  public String instrumentMethod(String $1) {
    return null;
  }
}
