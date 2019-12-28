name := "BoxSDKExamples"
version := "1.0"
scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
    "com.box" % "box-java-sdk" % "2.38.0",
    "com.typesafe.akka" %% "akka-actor" % "2.5.25",
    "com.typesafe.akka" %% "akka-http" % "10.1.10",
    "com.typesafe.akka" %% "akka-stream" % "2.5.25",
    "com.typesafe.play" %% "play-json" % "2.7.3"
)
