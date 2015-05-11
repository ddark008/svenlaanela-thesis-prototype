package sample;

class Parent {
  public Parent(String s) {}
}

public class SampleClass extends Parent {
  private final String instanceField = "InstanceField";
  
  public SampleClass() {
    super("default");
  }
  
  public SampleClass(String s) {
    super(s);
    System.out.println(s);
    // after
  }
  
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