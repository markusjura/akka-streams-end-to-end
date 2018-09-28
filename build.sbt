// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `akka-streams-end-to-end` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      Compile / packageDoc / publishArtifact := false,
      Compile / packageSrc / publishArtifact := false,
      libraryDependencies ++= Seq(
        library.akkaHttp,
        library.akkaHttpCirce,
        library.akkaLog4j,
        library.akkaStreamTyped,
        library.circeGeneric,
        library.disruptor,
        library.log4jApiScala,
        library.log4jCore,
        library.pureConfig,
        library.streamee
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka           = "2.5.16"
      val akkaHttp       = "10.1.5"
      val akkaHttpJson   = "1.22.0"
      val akkaLog4j      = "1.6.1"
      val akkaManagement = "0.18.0"
      val circe          = "0.10.0"
      val disruptor      = "3.4.2"
      val log4j          = "2.11.1"
      val log4jApiScala  = "11.0"
      val pureConfig     = "0.9.2"
      val scalaCheck     = "1.14.0"
      val streamee       = "4.0.0-RC2"
    }
    val akkaHttp                       = "com.typesafe.akka"             %% "akka-http"                         % Version.akkaHttp
    val akkaHttpCirce                  = "de.heikoseeberger"             %% "akka-http-circe"                   % Version.akkaHttpJson
    val akkaLog4j                      = "de.heikoseeberger"             %% "akka-log4j"                        % Version.akkaLog4j
    val akkaStreamTyped                = "com.typesafe.akka"             %% "akka-stream"                       % Version.akka
    val circeGeneric                   = "io.circe"                      %% "circe-generic"                     % Version.circe
    val disruptor                      = "com.lmax"                      %  "disruptor"                         % Version.disruptor
    val log4jApi                       = "org.apache.logging.log4j"      %  "log4j-api"                         % Version.log4j
    val log4jApiScala                  = "org.apache.logging.log4j"      %% "log4j-api-scala"                   % Version.log4jApiScala
    val log4jCore                      = "org.apache.logging.log4j"      %  "log4j-core"                        % Version.log4j
    val pureConfig                     = "com.github.pureconfig"         %% "pureconfig"                        % Version.pureConfig
    val scalaCheck                     = "org.scalacheck"                %% "scalacheck"                        % Version.scalaCheck
    val streamee                       = "io.moia"                       %% "streamee"                          % Version.streamee
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.6",
    organizationName := "Markus Jura",
    startYear := Some(2018),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ypartial-unification"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )
