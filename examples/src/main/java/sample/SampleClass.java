package sample;

public class SampleClass {
  private final String instanceField = "InstanceField";
  
  private final String instanceMethod(String input) {
    return input + instanceField;
  }
  
  public String publicMethod(String input) {
    return instanceMethod(input);
  }
}