package sample;

public class FieldInitDep {
  private String first = "First";
  private String second;
  public FieldInitDep() {
    String s = first + ",";
    second = s + "Second";
  }
}
