package sample.other;

public class PublicClass {
  public final String publicInstanceVariable = "publicInstanceVariable";
  public final static String publicStaticVariable = "publicStaticVariable";

  public final String publicInstanceMethod(String input) {
    return input + publicInstanceVariable;
  }
  public final static String publicStaticMethod(String input) {
    return input + publicStaticVariable;
  }
}