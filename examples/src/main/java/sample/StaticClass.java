package sample;

public class StaticClass {
  private static String staticField = "staticField";
  private static String staticMethod(String input) {
    return input + staticField;
  }
}
