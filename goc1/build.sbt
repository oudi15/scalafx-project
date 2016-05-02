// Name of the project
name := "AppManager"
 
// Project version
version := "0.1.0"
 
// Version of Scala used by the project
scalaVersion := "2.11.5"
 
// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.31-R7"

// Run in separate VM, so there are no issues with double initialization of JavaFX
fork := true

// Add dependency on JavaFX library based on JAVA_HOME variable
unmanagedJars in Compile += Attributed.blank(file(scala.util.Properties.javaHome) / "/lib/jfxrt.jar")
