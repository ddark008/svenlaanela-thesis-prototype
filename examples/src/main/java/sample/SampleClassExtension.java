package sample;

public class SampleClassExtension extends SampleClass {
  @Override
  public String publicMethod(String input) {
    if (input == null) {
      return null;
    }
    return super.publicMethod(input);
  }
}