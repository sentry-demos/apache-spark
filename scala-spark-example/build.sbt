import sbt._
import Keys._

val sparkVersion = "2.4.4"
val sentryVersion = "1.7.27"

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "example",
  // Semantic versioning http://semver.org/
  version := "1.0",
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq("-target:jvm-1.8",
                        "-deprecation",
                        "-feature",
                        "-unchecked"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "scala-spark-example",
    description := "Demo using Sentry with ScalaSpark",
    publish / skip := true,
    run / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % "2.4.4",
      "io.sentry" % "sentry" % "1.7.27",
    )
  )
