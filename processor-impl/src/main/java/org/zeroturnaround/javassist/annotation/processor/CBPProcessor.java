package org.zeroturnaround.javassist.annotation.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.zeroturnaround.javassist.annotation.Patches;
import org.zeroturnaround.javassist.annotation.processor.model.OriginalClass;

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
			
			JavaFileObject cbpClass = processingEnv.getFiler().createSourceFile(original.getName() + "CBP", null);
			PrintWriter w = new PrintWriter(new BufferedWriter(cbpClass.openWriter()));
			
			OriginalClass oc = new OriginalClass();
			oc.packageName = original.getPackageName();
			oc.name = original.getName();
			oc.cbpName = original.getName() + "CBP";
			oc.cbpSimpleName = original.getSimpleName() + "CBP";
			oc.companion.name = companionClass.toString();
			
			
			Velocity.addProperty(Velocity.RESOURCE_LOADER, "classpath");
			Velocity.addProperty("classpath.resource.loader.class" ,ClasspathResourceLoader.class.getName());
			
			Context ctx = new VelocityContext();
			ctx.put("lol", "wat");
			ctx.put("original", oc);
			ctx.put("companion", oc.companion);
			Velocity.mergeTemplate("cbp.vtl", "utf-8", ctx, w);
			
			w.close();
		} catch (NotFoundException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
			System.out.println(">> NotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
			System.out.println(">> IOException");
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println(">> Throwable" + e.getMessage());
		}
		
		System.out.println("Finished generating CBP for class: " + originalClass);
		
	}
	
	private void generateMirrorClass(TypeMirror companionClass, TypeMirror originalClass) {
		try {
			ClassPool classPool = ClassPool.getDefault();
			classPool.insertClassPath(new ClassClassPath(this.getClass()));
			CtClass ctClass = classPool.get(originalClass.toString());
		
			String mirrorClassQualifiedName = ctClass.getName() + "_Mirror";
			
			JavaFileObject mirrorClass = processingEnv.getFiler().createSourceFile(mirrorClassQualifiedName, null);

			PrintWriter w = new PrintWriter(new BufferedWriter(mirrorClass.openWriter()));
			w.println("package " + ctClass.getPackageName() + ";");
			w.println("");
			
			generateMirrorClass(ctClass, w);
			
			w.flush();
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("failure generating class");
		}
	}

	//TODO: Refactor to separate mirror generator class and add unit tests
	private void generateMirrorClass(CtClass ctClass, PrintWriter w) throws Exception {
		System.out.println("Generating mirror for class: " + ctClass.getName());
		CtClass superClass = ctClass.getSuperclass();
		String mirrorClassName = ctClass.getSimpleName() + "_Mirror";
		if (mirrorClassName.contains("$")) {
			mirrorClassName = mirrorClassName.substring(mirrorClassName.lastIndexOf('$') +1);
		}
		
		{
			int modifiers = ctClass.getModifiers();
			if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
				modifiers = Modifier.setPublic(modifiers);
			}
			if (Modifier.isInterface(modifiers)) {
				modifiers = Modifier.clear(modifiers, Modifier.ABSTRACT);
			}
			modifiers = Modifier.clear(modifiers, Modifier.FINAL);
			
			String addClass = Modifier.isInterface(modifiers) ? " " : " class "; 
			if (superClass == null || "java.lang.Object".equals(superClass.getName())) {
				w.println(Modifier.toString(modifiers) + addClass + mirrorClassName + " {");
			} else {
				String superClassName = superClass.getName().replace("$", ".");
				w.println(Modifier.toString(modifiers) + addClass + mirrorClassName + " extends "+superClassName+" {");
			}
		}
		
		for (CtClass nestedClass : ctClass.getDeclaredClasses()) {
			String innerClassName = nestedClass.getName().substring(ctClass.getName().length() + 1);
			System.out.println("Nested class: " + innerClassName);
			try {
				Integer.parseInt(innerClassName); //anonymous inner class
			} catch (NumberFormatException e) {
				generateMirrorClass(nestedClass, w);
			}
		}
		
		
		// add constructors, convert to public for access/override
		for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
			System.out.println("Adding constructor for " + mirrorClassName + " " + constructor.getModifiers() + " " + Modifier.toString(constructor.getModifiers()));
			System.out.println("Checking for synthetic");
			if ((constructor.getModifiers() & AccessFlag.SYNTHETIC) != 0 || constructor.getModifiers() == 0) { //wtf
				System.out.println("is synthetic");
				continue;
			}
			String s = "public " + mirrorClassName + "(\n";
			for (int i = 0; i < constructor.getParameterTypes().length; i++) {
				int modifiers = constructor.getModifiers();
				if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
					modifiers = Modifier.setPublic(modifiers);
				}
				
				CtClass parameter = constructor.getParameterTypes()[i];
				if (i != 0) s+=", ";
				String parameterName = toMirrorSafeName(ctClass, parameter);
				s+= parameterName + " $"+(i+1);
			}
			s+=") {";
			
			// find eligible super constructor
			for (CtConstructor superConstructor : superClass.getDeclaredConstructors()) {
				if (Modifier.isPublic(superConstructor.getModifiers())) {
					s+="  super(";
					for (int i = 0; i < superConstructor.getParameterTypes().length; i++) {
						if (i != 0) s+=", ";
						s+="null";
					}
					s+=");";
					break;
				}
			}
			
			s+="}";
			w.println(s);
		}
		
		// convert all class fields to public for access
		for (CtField field : ctClass.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
				modifiers = Modifier.setPublic(modifiers);
			}
			
			String typeName = toMirrorSafeName(ctClass, field.getType());
			
			w.println(Modifier.toString(modifiers) + " " + typeName + " " + field.getName() + " = "+ getDefaultValue(field.getType()) +";");
		}
		
		// convert all methods to public non-final for access/override
		for (CtMethod method : ctClass.getDeclaredMethods()) {
			System.out.println("Adding method for " + mirrorClassName + " " + method.getModifiers() + " " + Modifier.toString(method.getModifiers()));
			System.out.println("Checking for synthetic");
			int modifiers = method.getModifiers();
			if ((method.getModifiers() & AccessFlag.SYNTHETIC) != 0 || method.getName().startsWith("access$")) { //wtf
				System.out.println("is synthetic");
				continue;
			}
			if (Modifier.isPrivate(modifiers) || Modifier.isPackage(modifiers)) {
				modifiers = Modifier.setPublic(modifiers);
			}
			modifiers = Modifier.clear(modifiers, Modifier.FINAL);

			String returnType = toMirrorSafeName(ctClass, method.getReturnType());
			w.print(Modifier.toString(modifiers) + " " + returnType + " " + method.getName() + "(");
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				CtClass parameter = method.getParameterTypes()[i];
				if (i != 0) w.print(", ");
				String parameterName = toMirrorSafeName(ctClass, parameter);
				w.print(parameterName + " $"+(i+1));
			}
			w.print(")");
			if (Modifier.isAbstract(modifiers)) {
				w.print(";");
			} else {
				w.print("{");
				String defaultValue = getDefaultValue(method.getReturnType());
				if (defaultValue != null) {
					w.println("return " + defaultValue + ";");
				}
				w.println("}");
			}
		}
		
		w.println("}");
	}
	
	private String toMirrorSafeName(CtClass containingClass, CtClass type) throws NotFoundException {
		if (Arrays.asList(containingClass.getDeclaredClasses()).contains(type)) {
			return type.getName().substring(type.getName().lastIndexOf('$')+1) + "_Mirror";
		} else {
			return type.getName();
		}
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
