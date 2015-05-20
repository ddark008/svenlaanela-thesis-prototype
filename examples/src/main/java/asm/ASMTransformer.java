package asm;

import java.io.ByteArrayInputStream;

import javassist.ClassPool;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import sample.SampleClass;

public class ASMTransformer {
  @Test
  public void testReplaceMethodBody() throws Exception {
    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("instanceMethod") && desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
          mv.visitCode();
          mv.visitLdcInsn("Hello world!");
          mv.visitInsn(Opcodes.ARETURN);
          mv.visitMaxs(2, 2);
          mv.visitEnd();
          return null;
        }
 
        return mv;
      }
    };
    ClassReader reader = new ClassReader(this.getClass().getClassLoader().getResourceAsStream("sample/SampleClass.class"));
    reader.accept(visitor, 0);
    final byte[] modifiedBytecode = writer.toByteArray();
    
    SampleClass sampleClass = (SampleClass) toClass(modifiedBytecode);
    Assert.assertEquals("Hello world!", sampleClass.publicMethod("Test"));
  }
  
  private Object toClass(byte[] bytecode) throws Exception {
    ClassPool cp = new ClassPool();
    return cp.makeClass(new ByteArrayInputStream(bytecode)).toClass().newInstance();
  }
}
