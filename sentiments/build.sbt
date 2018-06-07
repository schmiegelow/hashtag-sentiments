
scalaVersion := "2.12.3"

libraryDependencies ++= {
  val kafkaV = "1.0.1"
  Seq(
    "org.apache.kafka" % "kafka-streams" % kafkaV,
    "com.google.cloud" % "google-cloud-translate" % "1.26.0"
  )
}

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-unchecked",
)

excludeDependencies ++= {
  Seq("org.slf4j" % "slf4j-log4j12", "com.sun.jmx" % "jmxri")
}

lazy val dockerSettings = Seq(
  packageName in Docker := "converter",
  packageSummary in Docker := "converter service",
  packageDescription := "Docker converter service",
  version in Docker := version.value.split("-").head
)

import com.typesafe.sbt.packager.docker.{Cmd, _}

dockerCommands := Seq()

dockerCommands := Seq(
  Cmd("FROM", "openjdk:latest"),
  Cmd("LABEL", s"""MAINTAINER="${maintainer.value}""""),
  Cmd("RUN", "apt-get update && apt-get install -y curl supervisor"),
//  Cmd("RUN", "CLOUD_SDK_REPO=\"cloud-sdk-$(lsb_release -c -s)\" echo \"deb http://packages.cloud.google.com/apt $CLOUD_SDK_REPO main\" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -"),
//  Cmd("RUN", "apt-get update && apt-get install google-cloud-sdk && apt-get install google-cloud-sdk-app-engine-java && gcloud init"),
  Cmd("COPY", "supervisord.conf /etc/supervisor/conf.d/"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt /opt"),
  Cmd("RUN", "chown", "-R", "daemon:daemon", "."),
  Cmd("RUN", "chown", "-R", "daemon:daemon", "/var/log/supervisor/"),
  Cmd("ENTRYPOINT", "/usr/bin/supervisord", "-n"),
  ExecCmd("CMD", ""),
  Cmd("ARG version=local"),
  Cmd("ENV VERSION $version"),
  Cmd("ARG commit"),
  Cmd("LABEL commit='${commit}'")
)

