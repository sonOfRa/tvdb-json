import play.Project._

name := "tvdb-json"

version := "1.0-SNAPSHOT"

// The Typesafe repository
resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spy Repository" at "http://files.couchbase.com/maven2"
)

libraryDependencies ++= Seq(
  "org.projectlombok" % "lombok" % "1.12.6" % "provided" withJavadoc() withSources(),
  "org.apache.httpcomponents" % "httpclient" % "4.3.3" withJavadoc() withSources(),
  "org.apache.httpcomponents" % "httpcore" % "4.3.2" withJavadoc() withSources(),
  "org.hibernate" % "hibernate-entitymanager" % "4.3.5.Final",
  "org.hibernate" % "hibernate-c3p0" % "4.3.5.Final",
  filters,
  javaJpa
)

playJavaSettings
