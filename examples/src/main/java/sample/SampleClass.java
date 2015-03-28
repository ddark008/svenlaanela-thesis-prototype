package sample;

public class SampleClass {
  private final String instanceVariable = "InstanceField";
  private final static String staticVariable = "StaticField";

  public String anotherMethod(String input) {
    System.out.println("Another method: " + input);
    return input;
  }
  
  public String publicMethod(String input) {
    System.out.println("Public method: " + input);
    return input;
  }
  
  private final String instanceMethod(String input) {
    return input + instanceVariable;
  }

  private static final String staticMethod(String input) {
    return input + staticVariable;
  }

  public final String instanceAccessor(String input) {
    return instanceMethod(input);
  }

  public static final String staticAccessor(String input) {
    return staticMethod(input);
  }
}

class SampleClassExtension extends SampleClass {
  @Override
  public String publicMethod(String input) {
    anotherMethod(input);
    return super.publicMethod(input);
  }
}