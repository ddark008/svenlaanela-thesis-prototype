package ee.lanza.javassist.annotation.processor.usecases.instrument;

import ee.lanza.javassist.annotation.processor.usecases.Util;

public class InstrumentClass {
  
  static {
    System.out.println("static constructor!");
  }
  
  private Other other = new Other();

  public String accessorLocal(String input) {
//    String r = doReverse2(input);
//    System.out.println(r);
    return doReverse1(input);
  }
  
  public String accessorOther(String input) {
//    String r = other.doReverse2(input);
//    System.out.println(r);
    return other.doReverse1(input);
  }

  private String doReverse1(String input) {
    return Util.reverse(input + "1");
  }

  private String doReverse2(String input) {
    return Util.reverse(input + "2");
  }

}

class Other {
  String doReverse1(String input) {
    return Util.reverse(input + "1");
  }

  String doReverse2(String input) {
    return Util.reverse(input + "2");
  }
}