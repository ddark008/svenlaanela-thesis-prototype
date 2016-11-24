Prototype implementation for Annotation-based Javassist.
========================================================

The goal of this prototype is to validate the technical feasibility of building a type-safe layer for classfile modifications on top of Javassist.


Modules
=======

processor-api - contains the API annotation interfaces for writing extension classes.
processpr-impl - contains the annotation processor code for compile-time class validation, mirror class generation and javassist wiring code generation.
processor-test - contains test classes for validating processor operation
processor-sample - contains some JRebel plugin classes rewritten with the annotation-based processor


Building/running
================

Run "mvn clean package".


Testing
=======

Integration tests are located under processor-test module. These attempt to test scenarios where a specific extension class for a given (original) class either fails with a
compilation error (listing the row and column numbers where the error occurred) or succeeds. If it succeeded, then the modifications that were described in the extension class
are verified by loading the original class, the extension class and a generated wiring class into an ad-hoc throwaway classloader and executing the method on the original class.

NB! The integration tests use a fork of the Google compile-testing library with some added functionality and is available under https://github.com/svenlaanela/compile-testing.
Unfortunately this pull request has not been merged to the project as of yet.