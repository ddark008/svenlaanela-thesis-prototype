package org.zeroturnaround.javassist.annotation.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Not thread-safe!
 */
public class MirrorClassRegistry {
  private Map<String, String> originalToMirror = new HashMap<String, String>();
  private Map<String, String> mirrorToOriginal = new HashMap<String, String>();
  
  public void registerMirrorClass(String originalClassName, String mirrorClassName) {
    originalToMirror.put(originalClassName, mirrorClassName);
    mirrorToOriginal.put(mirrorClassName, originalClassName);
  }

  public boolean contains(String originalClassName) {
    return originalToMirror.containsKey(originalClassName);
  }
  
  public boolean containsMirror(String mirrorClassName) {
    return mirrorToOriginal.containsKey(mirrorClassName);
  }
  
  public String getMirror(String originalClassName) {
    return originalToMirror.get(originalClassName);
  }
  
  public String getOriginal(String mirrorClassName) {
    return mirrorToOriginal.get(mirrorClassName);
  } 
}
