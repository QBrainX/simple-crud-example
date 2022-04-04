
name := "simple-crud-example"
version := "0.0.1"
organization := "com.qbrainx"

scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.9"
val SlickVersion = "3.3.3"

// Akka
libraryDependencies ++=
  Seq("com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion)

// Akka Http
libraryDependencies ++=
  Seq("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion)

// Slick
libraryDependencies ++=
  Seq("com.typesafe.slick" %% "slick" % SlickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion)

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.36"
libraryDependencies += "com.h2database" % "h2" % "2.1.210"
