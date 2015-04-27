package org.zeroturnaround.javassist.annotation.processor.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

public class ElementUtil {
  
  public static void printElement(Element element) {
    System.out.println(printElement(element, 0));
  }

  public static String printElement(Element element, int offset) {
    String print = "";
    for (int i = 0; i < offset; i++) {
      print += "  ";
    }
    print += element + ", " + element.asType() + "\n";
    if (element instanceof ExecutableElement) {
      ExecutableElement executableElement = ((ExecutableElement) element);
      for (int i = 0; i < offset; i++) {
        print += "  ";
      }
      print += "Parameters:\n";
      for (VariableElement paramElement : executableElement.getParameters()) {
        print += printElement(paramElement, offset + 1);
      }
      for (int i = 0; i < offset; i++) {
        print += "  ";
      }
      print += "TypeParameters:\n";
      for (TypeParameterElement typeParamElement : executableElement.getTypeParameters()) {
        print += printElement(typeParamElement, offset + 1);

      }
    }
    for (Element subElement : element.getEnclosedElements()) {
      print += printElement(subElement, offset + 1);
    }
    return print + "\n";
  }

}
