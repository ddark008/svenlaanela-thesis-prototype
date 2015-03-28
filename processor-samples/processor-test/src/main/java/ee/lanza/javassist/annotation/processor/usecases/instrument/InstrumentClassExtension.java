package ee.lanza.javassist.annotation.processor.usecases.instrument;

import org.zeroturnaround.javassist.annotation.MethodCall;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(InstrumentClass.class)
public class InstrumentClassExtension extends InstrumentClass_Mirror {

  @Override
  public String accessorLocal(String $1) {
    super.instrument(new MethodCall() {
      public String doReverse1(InstrumentClass_Mirror $0, String $1) {
        return $0.doReverse2($1);
      }
    });
    return super.accessorLocal($1);
  }

  @Override
  public String accessorOther(String $1) {
    super.instrument(new MethodCall() {
      public String doReverse1(Other $0, String $1) {
        return $0.doReverse2($1);
      }
    });
    return super.accessorOther($1);
  }
}
