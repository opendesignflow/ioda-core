

buildscript {
    dependencies {
        classpath("org.odfi.indesign:indesign-core:1.3.3")
        // classpath ("org.odfi.indesign:indesign-core:$indesign_version")
        classpath("org.odfi.ubroker:ubroker-core:1.1.0")
    }
}

plugins {

    id("java-library")

    // OOXOO
    id("org.odfi.ooxoo") version "3.4.4"

    // Publish
    id("maven-publish")


    // Scala
    id("scala")

}

var lib_version : String by rootProject.extra
println("V: $lib_version")
version = lib_version
group = "org.odfi.ioda"
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

// Dependencies
//-------------------

var indesign_version = "1.3.3"
val scala_major : String by rootProject.extra
val scala_minor : String by rootProject.extra
val scala_version : String by extra("$scala_major.$scala_minor")

//var indesign_version = "1.3.3"
var ubroker_version = "1.1.0"

//var scala_version = gradle.ext.has("scala_version") ? gradle.ext.scala_version : "$scala_major.6"
dependencies {


    // ODFI
    //----------

    // Dependencies that can be build alongside the project
    if (findProject(":ubroker-core") != null) {
        api(project(":ubroker-core"))

    } else {
        api("org.odfi.ubroker:ubroker-core:$ubroker_version")
    }
    if (findProject(":indesign-core") != null) {
        api(project(":indesign-core"))
    } else {
        api("org.odfi.indesign:indesign-core:$indesign_version")

        //api "org.odfi.ioda.instruments:instruments-core:$instruments_version"
    }


    // External dependencies
    //-------------
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.0")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.0")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    api("org.jmdns:jmdns:3.5.6")

    api("org.apache.logging.log4j:log4j-api:2.14.1")
    api("org.apache.logging.log4j:log4j-core:2.14.1")
    api("org.apache.logging.log4j:log4j-api-scala_$scala_major:12.0")
    api("org.fusesource.jansi:jansi:2.1.1")

    api("org.apache.httpcomponents:fluent-hc:4.5.13")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    // api group: 'com.google.code.gson', name: 'gson', version: '2.8.6'

    api("org.scala-lang:scala-library:$scala_version")
}


repositories {

    mavenLocal()
    mavenCentral()
    maven {
        name = "Sonatype Nexus Snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "ODFI Releases"
        url = uri("https://www.opendesignflow.org/maven/repository/internal/")
    }
    maven {
        name = "ODFI Snapshots"
        url = uri("https://www.opendesignflow.org/maven/repository/snapshots/")
    }
    maven {
        url = uri("https://repo.triplequote.com/libs-release/")
    }
    google()
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

        /*publishToMavenLocal(MavenPublication) {



            pom {
                name = "IODA Core"
                description = "IODA Core module"

                //properties = [ ]
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "richnou"
                        name = "Richnou"
                        email = "leys.richard@gmail.com"
                    }
                }

            }
        }*/

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
                password = System.getenv ("PUBLISH_PASSWORD")
            }
        }
    }
}