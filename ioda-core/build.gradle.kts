buildscript {
    dependencies {
        classpath("org.odfi.indesign:indesign-core:2.0.0-SNAPSHOT")
        // classpath ("org.odfi.indesign:indesign-core:$indesign_version")
        classpath("org.odfi.ubroker:ubroker-core:2.0.0")
    }
}

plugins {

    // Scala
    id("scala")

    // OOXOO
    id("org.odfi.ooxoo") version "4.0.1"

    // Publish
    id("maven-publish")

    id("java-library")


}

//version = gradle.ext.has("version") ? gradle.ext.version : "dev"

// Sources
//-------------------
sourceSets {
    main {
        scala {
            // Generated from ooxoo
            srcDir(File(buildDir, "generated-sources/scala"))
            //srcDir new java.io.File(getBuildDir(), "generated-sources/scala")
        }
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

// Scala compilation options
tasks.withType<ScalaCompile>().configureEach {
    scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}

// Dependencies
//-------------------

val ooxooVersion: String by rootProject.extra
val indesignVersion: String by rootProject.extra
val ubrokerVersion: String by rootProject.extra


dependencies {


    // ODFI
    //----------
    api("org.odfi.ubroker:ubroker-core:$ubrokerVersion")
    api("org.odfi.indesign:indesign-stdplatform:$indesignVersion")

    // External dependencies
    //-------------
    val jacksonVersion = "2.12.5"
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

    api("org.jmdns:jmdns:3.5.7")

    api("org.apache.logging.log4j:log4j-api:2.14.1")
    api("org.apache.logging.log4j:log4j-core:2.14.1")
    //api("org.apache.logging.log4j:log4j-api-scala_$scalaMajor:12.0")
    api("org.fusesource.jansi:jansi:2.3.4")

    api("org.apache.httpcomponents:fluent-hc:4.5.13")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    // api group: 'com.google.code.gson', name: 'gson', version: '2.8.6'

    //api("org.scala-lang:scala-library:$scala_version")
}




publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ioda-core"
            from(components["java"])

            pom {
                name.set("IODA Core")
                description.set("IODA Core module")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("richnou")
                        name.set("Richnou")
                        email.set("leys.richard@gmail.com")
                    }
                }
            }
        }

    }
    repositories {
        maven {

            // change URLs to point to your repos, e.g. http://my.org/repo
            var releasesRepoUrl = uri("https://www.opendesignflow.org/maven/repository/internal/")
            var snapshotsRepoUrl = uri("https://www.opendesignflow.org/maven/repository/snapshots")

            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            // Credentials
            //-------------
            credentials {
                username = System.getenv("PUBLISH_USERNAME")
                password = System.getenv("PUBLISH_PASSWORD")
            }
        }
    }
}