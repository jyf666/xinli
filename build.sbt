name := "xinli"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

organization := "be.objectify"

libraryDependencies ++= Seq(
  javaJdbc,
//  javaJpa,
//  javaEbean,
  cache,
  "org.hibernate" % "hibernate-entitymanager" % "5.0.5.Final",
  "org.hibernate" % "hibernate-ehcache" % "5.0.5.Final",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "org.springframework.data" % "spring-data-jpa" % "1.8.2.RELEASE",
  "javax.inject" % "javax.inject" % "1",
  "commons-beanutils" % "commons-beanutils" % "1.9.2",
  "org.apache.poi" % "poi" % "3.12",
  "org.apache.poi" % "poi-ooxml" % "3.12",
  "javax.mail" % "mail" % "1.4.7",
  "com.lowagie" % "itext" % "4.2.2",
  "be.objectify" %% "deadbolt-java" % "2.3.2",
  "commons-codec" % "commons-codec" % "1.9",
  "commons-io" % "commons-io" % "2.4"
//  "com.kryshchuk.mirror.be.objectify" % "deadbolt-java_2.11" % "2.3.1"
//  "be.objectify" %% "deadbolt-java" % "2.4.3"
)  

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator
//play.Project.playJavaSettings