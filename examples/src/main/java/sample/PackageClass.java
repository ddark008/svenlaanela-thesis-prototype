package sample;

class PackageClass {
  final String packageInstanceVariable = "packageInstanceVariable";
  final static String packageStaticVariable = "packageStaticVariable";

  final String packageInstanceMethod(String input) {
    return input + packageInstanceVariable;
  }

  final static String packageStaticMethod(String input) {
    return input + packageStaticVariable;
  }
}