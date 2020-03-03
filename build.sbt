import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.mbari"
ThisBuild / organizationName := "MBARI"
ThisBuild / licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

lazy val consoleSettings = Seq(
  shellPrompt := { state =>
    val user = System.getProperty("user.name")
    user + "@" + Project.extract(state).currentRef.project + ":sbt> "
  }
)

lazy val dependencySettings = Seq(
  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.bintrayRepo("org-mbari", "maven")
  )
)

lazy val optionSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", // 2 args
    "UTF-8",     // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xlint"
  ),
  javacOptions ++= Seq("-target", "11", "-source", "11")
)

lazy val settings = consoleSettings ++ dependencySettings ++ optionSettings



lazy val root = (project in file("."))
  .settings(settings)
  .settings(
    name := "cthulu-phase1",
    mainClass in assembly := Some("org.mbari.cthulu.phase1.App"),
    assemblyJarName := "cthulu-phase1-app.jar",
    assemblyMergeStrategy in assembly := {
      case "module-info.class" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    libraryDependencies ++= Seq(
      circeConfig,
      logback,
      picocli,
      scalactic % "test",
      scalaTest % "test",
      slf4j,
      vcr4jSharktopoda,
      vcr4jSharkClient
    )
  )
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
