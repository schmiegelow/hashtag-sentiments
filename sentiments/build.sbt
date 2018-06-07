
scalaVersion := "2.12.3"

libraryDependencies ++= {
  val kafkaV = "1.0.1"
  Seq(
    "org.apache.kafka" % "kafka-streams" % kafkaV,
    "com.google.cloud" % "google-cloud-translate" % "1.26.0",
    "com.google.cloud" % "google-cloud-language" % "1.31.0",
    "com.typesafe.play" %% "play-json" % "2.6.9"
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
  Cmd("RUN", "apt-get update && apt-get install -y curl supervisor lsb-release"),
  Cmd("RUN", "echo \"deb http://packages.cloud.google.com/apt cloud-sdk-$(lsb_release -c -s) main\" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -"),
  Cmd("RUN", "apt-get update && apt-get install -y google-cloud-sdk google-cloud-sdk-app-engine-java"),
  Cmd("RUN", "ln -s /opt/docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt /opt"),
  Cmd("RUN", "chown", "-R", "daemon:daemon", "."),
  Cmd("RUN", "chown", "-R", "daemon:daemon", "/var/log/supervisor/"),
  Cmd("ENTRYPOINT", "/usr/bin/supervisord", "-n", "-c", "/etc/supervisor/supervisord.conf"),
  ExecCmd("CMD", ""),
  Cmd("ARG version=local"),
  Cmd("ENV VERSION $version"),
  Cmd("ENV GOOGLE_APPLICATION_CREDENTIALS /opt/docker/hashtag-sentiments.json"),
  Cmd("ARG commit"),
  Cmd("LABEL commit='${commit}'")
)

