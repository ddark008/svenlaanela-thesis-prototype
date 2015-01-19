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