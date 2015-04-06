package sample;

public class SampleClass {
  private final String instanceField = "InstanceField";
  
  private final String instanceMethod(String input) {
    return input + instanceField;
  }
  
  public final String publicMethod(String input) {
    return instanceMethod(input);
  }
}

class SampleClassExtension extends SampleClass {
  @Override
  public String publicMethod(String input) {
    anotherMethod(input);
    return super.publicMethod(input);
  }
}