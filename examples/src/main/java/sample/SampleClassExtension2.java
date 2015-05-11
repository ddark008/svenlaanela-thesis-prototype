package sample;

import org.zeroturnaround.javassist.annotation.Modify;
import org.zeroturnaround.javassist.annotation.Patches;

@Patches(SampleClass.class)
public class SampleClassExtension2 extends SampleClass_Mirror {
    
  @Modify
  public SampleClassExtension2(String s1, String s2) {
    s1 = call(s1);
    
//    invokeSuperConstructor(s1, s2);
    
    
    // additional code done "after" in the constructor
//    System.out.println(s);
  }
  
//  @BeforeSuper 
  public static String call(String s) {
    s = s.substring(2);
    return s;
  }
}
