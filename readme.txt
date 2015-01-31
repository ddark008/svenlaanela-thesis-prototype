Prototype implementation for Annotation-based Javassist.
========================================================

The goal of this prototype is to specify the technical feasibility
of building a type-safe layer for classfile modifications on top 
of Javassist


Modules
=======

procapi - contains the API annotation interfaces for interfacing with classes
procimpl - contains the annotation processor code for compile-time class validation,
mirror class generation and javassist wiring code generation
procsample - contains JRebel plugin classes and attempts to mirror those
proctest - contains test classes for validating processor operation


Building/running
================

Run "mvn clean package". This results in a compilation error being reported for proctest:

[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:2.4:compile (default-compile) on project proctest: Compilation failure
[ERROR] /Users/lanza/Projects/java/thesis/thesis-prototype/proctest/src/main/java/ee/lanza/test/AClass.java:[6,7] error: This classname is: AClass
[ERROR] -> [Help 1]

This error is expected (as proof of concept for annotation processor operation), 
see CBPProcessor.java in procimpl module


Running in IntelliJ
===================

*needs cleanup*
Import maven project
Disable automatic building for project
Rebuild project, shows the error in messages
(Have not been able to get this to show up in source code view for AClass.java)


Running in Eclipse
==================

*needs cleanup*
Import maven project
Build procimpl.jar
Set procimpl.jar as the annotation processor for proctest
Build proctest
Should show the compilation error in the source code view for AClass.java

After making changes, do the above again, cannot do those as single steps,
Eclipse does not pick up changes to the annotation processor for some reason :(


Prototype implementation plan
=============================

1. Read through Javassist API
2. Create a list of use-cases that the Javassist API allows to do
   - add new field
   - add new method
   - add before/after advice
   - etc.
3. For all the above use-cases, investigate how to provide type-safety
   for those use-cases working in conjuction with framework classes.
   (need to access private fields and methods in original class,
   as well as public, package-private, protected fields and methods
   in framework classes)
4. Implement generating a liberal mirror to extend from during
   compile-time
5. Create javassist wiring code for invoking methods/accessing fields on
   overriding class from the original class (and vice versa). Companion class/object?
6. Add compile-time validation errors for invalid constructs 



Use-cases that javassist allows and that annotationassist should be able to emulate:

Adding a method to class and from that:
* Call existing methods on the class (and non-private methods of superclasses)
* Call existing methods on other classes (based on visibility rules)
* Call other added methods on the class
* Call other added methods on other classes
* Access existing fields on the class (and non-private fields of superclasses)
* Access existing fields on other classes (based on visibility rules)
* Access added fields on the class
* Access added fields on other classes
Adding a field to class and from that

Inserting code to the beginning of an existing method
Inserting code to the end of an existing method
Modifying a specific method call inside an existing method

=======
Prototype implementation for Annotation-based Javassist.
========================================================

The goal of this prototype is to specify the technical feasibility
of building a type-safe layer for classfile modifications on top 
of Javassist


Modules
=======

procapi - contains the API annotation interfaces for interfacing with classes
procimpl - contains the annotation processor code for compile-time class validation,
mirror class generation and javassist wiring code generation
procsample - contains JRebel plugin classes and attempts to mirror those
proctest - contains test classes for validating processor operation


Building/running
================

Run "mvn clean package". This results in a compilation error being reported for proctest:

[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:2.4:compile (default-compile) on project proctest: Compilation failure
[ERROR] /Users/lanza/Projects/java/thesis/thesis-prototype/proctest/src/main/java/ee/lanza/test/AClass.java:[6,7] error: This classname is: AClass
[ERROR] -> [Help 1]

This error is expected (as proof of concept for annotation processor operation), 
see CBPProcessor.java in procimpl module


Running in IntelliJ
===================

*needs cleanup*
Import maven project
Disable automatic building for project
Clean/Make project, shows the error in compilation results
(Have not been able to get this to show up in source code view for AClass.java)


Running in Eclipse
==================

*needs cleanup*
Import maven project
Build procimpl.jar
Set procimpl.jar as the annotation processor for proctest
Build proctest
Should show the compilation error in the source code view for AClass.java

After making changes, do the above again, cannot do those as single steps,
Eclipse does not pick up changes to the annotation processor for some reason :(


Prototype implementation plan
=============================

1. Read through Javassist API
2. Create a list of use-cases that the Javassist API allows to do
   - add new field
   - add new method
   - add before/after advice
   - etc.
3. For all the above use-cases, investigate how to provide type-safety
   for those use-cases working in conjuction with framework classes.
   (need to access private fields and methods in original class,
   as well as public, package-private, protected fields and methods
   in framework classes - generate liberal mirror to extend from during
   compile-time?)
4. Create javassist wiring code for invoking methods/accessing fields on
   overriding class from the original class (and vice versa). Companion class/object?
5. Add compile-time validation errors for invalid constructs
