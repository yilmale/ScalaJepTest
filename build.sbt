name := "ScalaJepTest"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies  ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.0.0",
  "org.apache.spark" %% "spark-sql" % "3.0.0",
  "org.apache.spark" %% "spark-mllib" % "3.0.0",
  "org.apache.spark" %% "spark-streaming" % "3.0.0" % "provided",
  "org.apache.spark" %% "spark-graphx" % "3.0.0"

)