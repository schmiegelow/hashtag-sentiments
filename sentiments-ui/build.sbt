
organization := "com.hivemindtechnologies"
scalaVersion := "2.12.4"
version := "0.1.0-SNAPSHOT"
name := "profile id frontend"

enablePlugins(ScalaJSBundlerPlugin)
scalaJSUseMainModuleInitializer := true
scalaJSModuleKind := ModuleKind.CommonJSModule
skip in packageJSDependencies := false
npmDependencies in Compile += "file-saver" -> "1.3.3"

libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core",
  "io.circe" %%% "circe-generic",
  "io.circe" %%% "circe-parser"
).map(_ % "0.9.0")

libraryDependencies += "org.julienrf"   %%% "scalm"     % "1.0.0-RC1"
libraryDependencies += "org.scalactic"  %%% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest"  %%% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

webpackDevServerExtraArgs := Seq("--content-base", "../../../..", "--inline")
webpackDevServerPort := 12345

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfatal-warnings"
)
