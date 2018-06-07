

libraryDependencies ++= {
  val kafkaV = "1.0.1"
  Seq(
    "org.apache.kafka" % "kafka-streams" % kafkaV,
    "org.twitter4j" % "twitter4j-core" % "4.0.6"
  )
}


excludeDependencies ++= {
  Seq("org.slf4j" % "slf4j-log4j12", "com.sun.jmx" % "jmxri")
}

lazy val dockerSettings = Seq(
  packageName in Docker := "feeder",
  packageSummary in Docker := "feeder service",
  packageDescription := "Docker feeder service",
  version in Docker := version.value.split("-").head
)

import com.typesafe.sbt.packager.docker.{Cmd, _}

dockerCommands := Seq()

dockerCommands := Seq(
  Cmd("FROM", "openjdk:latest"),
  Cmd("LABEL", s"""MAINTAINER="${maintainer.value}""""),
  Cmd("RUN", "apt-get update && apt-get install -y curl && apt-get install -y supervisor"),
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

