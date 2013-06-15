import sbt._
import Keys._

object Build extends Build {

  lazy val root = Project(
    id = "piano",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      scalaVersion := "2.10.2",
      libraryDependencies ++= Seq(
        "com.typesafe" % "config" % "1.0.1",
        "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
      )
    )
  )

}
