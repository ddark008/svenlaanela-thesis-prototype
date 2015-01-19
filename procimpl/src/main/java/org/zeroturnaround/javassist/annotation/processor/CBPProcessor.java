package org.zeroturnaround.javassist.annotation.processor;

import java.util.Set;

import javax.annotation.processing.*;
import javax.tools.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.*;

@SupportedAnnotationTypes("org.zeroturnaround.javassist.annotation.Patches")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class CBPProcessor extends AbstractProcessor {
	
	public CBPProcessor() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement e : annotations) {
			System.out.println("TypeElement: " + e);
		}
		for (Element element : roundEnv.getRootElements()) {
			if (element.getSimpleName().toString().startsWith("A")) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "This classname is: " + element.getSimpleName(), element);
//				processingEnv.getFiler().createClassFile("");
			}
		}

		return true; // finish processing
	}

}
