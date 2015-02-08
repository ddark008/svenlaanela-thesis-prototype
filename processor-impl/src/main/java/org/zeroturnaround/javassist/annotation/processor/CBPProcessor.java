package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.zeroturnaround.javassist.annotation.Before;
import org.zeroturnaround.javassist.annotation.OriginalAware;
import org.zeroturnaround.javassist.annotation.Patches;

/**
 * First pass: Generate mirror classes for type safety
 * Second pass: Generate Javassist CBP classes based on method implementation
 * detect if we are overriding any methods
 * companion object
 *
 */
@SupportedAnnotationTypes("org.zeroturnaround.javassist.annotation.Patches")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CBPProcessor extends AbstractProcessor {
	
	public CBPProcessor() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (Element element : roundEnv.getRootElements()) {
				if (element.getKind() == ElementKind.CLASS && element.getAnnotation(Patches.class) != null) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Patching class: " + element.getSimpleName(), element);
		
					Patches annotation = element.getAnnotation(Patches.class);
					try {
						Class<?> value = annotation.value();
					} catch (MirroredTypeException e) {
						generateMirrorClass(element.asType(), e.getTypeMirror());
						generateCBPClass(e.getTypeMirror(), element.asType());
					}
					
				}
			}
			return true;
		} else {
			return false;
		}

//		return false;
	}
	
	//TODO: Refactor to separate CBP generator class and add unit tests
	//TODO: Companion class should be transformed to an inner class of the patched class during annotation processing.
	private void generateCBPClass(TypeMirror originalClass, TypeMirror companionClass) {
		
		System.out.println("Generating CBP for class: " + originalClass);
		ClassPool classPool = new ClassPool();
		classPool.insertClassPath(new ClassClassPath(this.getClass()));
		
		try {
			final CtClass original = classPool.get(originalClass.toString());
			
			/**
			 * TESTING try to write an inner class
			 */
			
			//classPool.get("plah");
//			CtClass nested = original.makeNestedClass("plah", false);
			
			// damn constructors!
			
			/**
			 * TESTING END
			 */

			CtMethod[] originalMethods = original.getDeclaredMethods();
			

			System.out.println(">>1");
			
			JavaFileObject cbpClass = processingEnv.getFiler().createSourceFile(original.getName() + "CBP", null);
			PrintWriter w = new PrintWriter(new BufferedWriter(cbpClass.openWriter()));
			w.println("package " + original.getPackageName() + ";");
			
			w.println("import javassist.*;");
			w.println("import javassist.expr.*;");
			
			w.println("public class " + original.getSimpleName() + "CBP implements " +JavassistClassBytecodeProcessor.class.getName()+ " {" );
			
			w.println("  public void process(ClassPool cp, ClassLoader cl, final CtClass ctClass) throws Exception {");

			// add companion object as field
			
			w.println("    ctClass.addField(CtField.make(\"final "+companionClass.toString()+" __companion = new "+companionClass.toString()+"();\" , ctClass));");
			
			
			//TODO: convert companion to inner class
//			w.println("    ctClass.makeNestedClass(\""+companionClass.toString().substring(companionClass.toString().lastIndexOf('.'))+"\", false);");
			w.println("    CtClass companionClass = cp.get(\""+companionClass.toString()+"\");");
			w.println("    companionClass.addField(CtField.make(\""+original.getName()+" __original = null;\", companionClass));");
			w.println("    companionClass.addInterface(cp.get(\""+OriginalAware.class.getName()+"\"));");
			w.println("    companionClass.addMethod(CtMethod.make(\"public void setOriginal(Object original) { __original = ("+original.getName()+") original; }\", companionClass));");
			
			w.println("    for (CtMethod method : companionClass.getDeclaredMethods()) {");
			w.println("        // override original method");
			w.println("        try {");
			w.println("            CtMethod originalMethod = ctClass.getDeclaredMethod(method.getName(), method.getParameterTypes());");
			w.println("            CtMethod originalCopy = CtNewMethod.copy(originalMethod, method.getName() + \"__original\", ctClass, null);");
			w.println("            ctClass.addMethod(originalCopy);");
			w.println("            if (\"void\".equals(originalMethod.getReturnType().getName())) {");
			w.println("                originalMethod.setBody(\"{ __companion.\"+method.getName()+\"($$);}\");");
			w.println("            } else {");
			w.println("                originalMethod.setBody(\"{ return __companion.\"+method.getName()+\"($$);}\");");
			w.println("            }");
			w.println("        } catch (NotFoundException ignored) { ignored.printStackTrace();}");
			w.println("        System.out.println(\"LeMethod:\"+method.getName());");
			w.println("        method.instrument(new ExprEditor() {");
			w.println("            public void edit(MethodCall m) throws CannotCompileException {");
			w.println("                System.out.println(\">Method:\"+m.getMethodName());");
			w.println("                System.out.println(\">Classname:\"+m.getClassName());");
			w.println("                System.out.println(\">Signature:\"+m.getSignature());");
			w.println("                if (m.getClassName().equals(ctClass.getName() + \"_Mirror\")) {");
			w.println("                    System.out.println(\"PATCHING!\");");
			w.println("                    String callFromOriginal = \"__original\";");
			w.println("                    //String callFromOriginal = \"+ctClass.getName()+\".this.\";");
			w.println("                    try {");
			w.println("                    if (\"void\".equals(m.getMethod().getReturnType().getName())) {");
			w.println("                        m.replace(\"{ \"+callFromOriginal+\".\" + m.getMethodName() + \"__original($$); }\");");
			w.println("                    } else {");
			w.println("                        m.replace(\"{ $_ = \"+callFromOriginal+\".\" + m.getMethodName() + \"__original($$); }\");");
			w.println("                    }");
			w.println("                    } catch (Exception e) { e.printStackTrace();}");
			w.println("                }");
			w.println("            }");
			w.println("            public void edit(FieldAccess f) throws CannotCompileException {");
			w.println("                System.out.println(\">Field:\"+f.getFieldName());");
			w.println("                System.out.println(\">Classname:\"+f.getClassName());");
			w.println("                System.out.println(\">Signature:\"+f.getSignature());");
			w.println("                if (f.getClassName().equals(ctClass.getName() +\"_Mirror\")) {");
			w.println("                    f.replace(\"{ $_ = \"+ctClass.getName() + \".this.\" + f.getFieldName() +\"; }\");");
			w.println("                }");
			w.println("            }");
			w.println("        });");
			w.println("    }");
			
			//TODO: temp
			w.println("    for (CtConstructor method : ctClass.getDeclaredConstructors()) {");
			w.println("    method.insertAfter(");
			w.println("    		\"{ \"+");
			w.println("    		\"    (("+OriginalAware.class.getName()+")__companion).setOriginal(this);\"+");
			w.println("    		\"}\");");
			w.println("    }");
			
			w.println("companionClass.writeFile();");
			w.println("ctClass.defrost();");
			w.println("ctClass.writeFile();");
			
			w.println("    companionClass.toClass();"); //TODO: it might be incorrect to call this in the context of the ContextClassLoader of a given thread.
			
			
			
//			Element companionElement = processingEnv.getTypeUtils().asElement(companionClass);
//
//			for (Element element : companionElement.getEnclosedElements()) {
//				if (element.getKind() == ElementKind.METHOD) {
//					Before before = element.getAnnotation(Before.class);
//					if (before != null) {
//						System.out.println("is before!");
//						ExecutableElement executable = (ExecutableElement) element;
//						for (CtMethod originalMethod : originalMethods) {
//							boolean signatureMatches = false;							
//							if (executable.getSimpleName().toString().equals(originalMethod.getName())) {
//								System.out.println("Name: " + executable.getSimpleName());
//								if (executable.getReturnType().toString().equals(originalMethod.getReturnType().getName())) {
//									System.out.println("Return type: " + executable.getReturnType().toString());
//									
//									List<String> originalParameterTypeNames = new ArrayList<String>();
//									for (CtClass parameterClass : originalMethod.getParameterTypes()) {
//										System.out.println("Javassist type: " + parameterClass.getName());
//										originalParameterTypeNames.add(parameterClass.getName());	
//									}
//									
//									if (originalParameterTypeNames.size() == executable.getParameters().size()) {
//										for (int i = 0; i < executable.getParameters().size(); i++) {
//											System.out.println("ExecutableElement type: " + executable.getParameters().get(i).asType().toString());
//											String companionParameterTypeName = executable.getParameters().get(i).asType().toString();
//											if (!originalParameterTypeNames.get(i).equals(executable.getParameters().get(i).asType().toString())) {
//												break;
//											}
//											if (i == executable.getParameters().size() -1) {
//												signatureMatches = true;
//											}
//										}
//										System.out.println("Signature matches? " + signatureMatches);
//										
//										w.println("{");
//										w.println("  CtClass[] params = cp.get(new String[] {");
//										boolean addComma = false;
//										for (String parameterTypeName : originalParameterTypeNames) {
//											w.println("    "+(addComma ? ", " : " ") +"\""+parameterTypeName+"\"");
//											addComma = true;
//										}
//										w.println("  });");
//										w.println("  ctClass.getDeclaredMethod(\""+originalMethod.getName()+"\", params).insertBefore(\"{ __companion."+originalMethod.getName()+"($$); }\");");
//										w.println("}");
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			
			
			// TODO!
//			CtClass companion = classPool.get(companionClass.toString()); // companion should be in the same package.
		
			System.out.println(">>2");
			
			
//			CtMethod[] companionMethods = companion.getDeclaredMethods();
			// if companionMethod signature is same as in parent class, treat it as override (before or after advice)
			// if call to super is last
			
			System.out.println(">>3");
			
//			for (CtMethod method : companionMethods) {
//				for (CtMethod originalMethod : originalMethods) {
//					if (originalMethod.equals(method)) {
//						System.out.println("Matching method: " + method.getSignature());
////						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Matching method: " + method.getSignature());
////						
////						originalMethod.insertBefore("__companion." + originalMethod.getName() + "($$);");
////						
//						break;
//					}
//				}
//			}
//			
//			System.out.println(">>4");
			
			w.println("  }");
			w.println("}");
			w.close();
		} catch (NotFoundException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
			System.out.println(">> NotFoundException");
			e.printStackTrace();
//		} catch (CannotCompileException e) {
//			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
//			System.out.println(">> CannotCompileException");
//			e.printStackTrace();
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
			System.out.println(">> IOException");
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println(">> Throwable" + e.getMessage());
		}
		
		System.out.println("Finished generating CBP for class: " + originalClass);
		
	}

	//TODO: Refactor to separate mirror generator class and add unit tests
	private void generateMirrorClass(TypeMirror companionClass, TypeMirror originalClass) {
		System.out.println("Generating mirror for class:" + originalClass);
		try {
			System.out.println("CL: " + this.getClass().getClassLoader());
			for (URL url : ((URLClassLoader)this.getClass().getClassLoader()).getURLs()) {
				System.out.println("URL: " + url);
			}


			ClassPool classPool = ClassPool.getDefault();
			classPool.insertClassPath(new ClassClassPath(this.getClass()));

			CtClass ctClass = classPool.get(originalClass.toString());
			
			String mirrorClassQualifiedName = ctClass.getName() + "_Mirror";
			String mirrorClassName = ctClass.getSimpleName() + "_Mirror";
			
			JavaFileObject mirrorClass = processingEnv.getFiler().createSourceFile(mirrorClassQualifiedName, null);

			PrintWriter w = new PrintWriter(new BufferedWriter(mirrorClass.openWriter()));
			w.println("package " + ctClass.getPackageName() + ";");
			w.println("");
			
			CtClass superClass = ctClass.getSuperclass();
			
			if (superClass == null) {
				w.println("public class " + mirrorClassName + " {");
			} else {
				w.println("public class " + mirrorClassName + " extends "+superClass.getName()+" {");
			}
			
			
			// convert all class fields to public for access
			for (CtField field : ctClass.getDeclaredFields()) {
				String addStatic = Modifier.isStatic(field.getModifiers()) ? "static " : "";
				String addFinal = Modifier.isFinal(field.getModifiers()) ? "final " : "";
				String addSynchronized = Modifier.isSynchronized(field.getModifiers()) ? "synchronized " : "";
				String addVolatile = Modifier.isVolatile(field.getModifiers()) ? "volatile " : "";
				String addTransient = Modifier.isTransient(field.getModifiers()) ? "transient " : "";
				String modifiers = addStatic + addFinal + addSynchronized + addVolatile + addTransient;
				w.println("public " + modifiers + field.getType().getName() + " " + field.getName() + " = "+ getDefaultValue(field.getType()) +";");
			}
			
			// convert all methods to public non-final for access/override
			for (CtMethod method : ctClass.getDeclaredMethods()) {
				String addStatic = Modifier.isStatic(method.getModifiers()) ? "static " : "";
//				String addFinal = Modifier.isFinal(field.getModifiers()) ? "final " : "";
				String addSynchronized = Modifier.isSynchronized(method.getModifiers()) ? "synchronized " : "";
//				String addVolatile = Modifier.isVolatile(method.getModifiers()) ? "volatile " : "";
//				String addTransient = Modifier.isTransient(method.getModifiers()) ? "transient " : "";
				String modifiers = addStatic + addSynchronized;
				w.print("public " + modifiers + method.getReturnType().getName() + " " + method.getName() + "(");
				for (int i = 0; i < method.getParameterTypes().length; i++) {
					CtClass parameter = method.getParameterTypes()[i];
					if (i != 0) w.print(", "); 
					String parameterName = parameter.getName();
					if (parameterName.startsWith(ctClass.getName())) { // is inner class TODO: inner class visibility
						parameterName = parameterName.replace('$', '.');
					}
					w.print(parameterName + " $"+(i+1));
				}
				w.println(") {");				
				String defaultValue = getDefaultValue(method.getReturnType());
				if (defaultValue != null) {
					w.println("return " + defaultValue + ";");
				}
				w.println("}");
			}
			w.println("}");
			w.flush();
			w.close();
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
//			e.printStackTrace();
		}
		System.out.println("Finished generating mirror for class:" + originalClass);
	}
	
	private String getDefaultValue(CtClass clazz) {
		String name = clazz.getName();
		if ("void".equals(name)) return null;
		else if ("char".equals(name)) return "'x'";
		else if ("short".equals(name)) return "0";
		else if ("int".equals(name)) return "0";
		else if ("long".equals(name)) return "0";
		else if ("boolean".equals(name)) return "false";
		else if ("float".equals(name)) return "0.0";
		else if ("double".equals(name)) return "0.0";
		else return "null";
	}
	
}
