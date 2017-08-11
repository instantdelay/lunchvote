name := "LunchVote"
 
version := "1.0" 
      
lazy val `lunchvote` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % Test,
  "junit" % "junit" % "4.12" % Test
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      