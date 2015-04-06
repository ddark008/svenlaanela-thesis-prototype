package sample;

public class InstrumentClass {
  private String field = "field";
  
  private String method1() {
    return "method1";
  }
  
  private String method2() {
    return "method2";
  }
  
  public String instrumentMethod() {
    return field + method1();
  }
}
