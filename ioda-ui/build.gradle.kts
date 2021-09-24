

plugins {
    id("java-library")

    // OOXOO
    id("org.odfi.ooxoo") version "4.0.2"

    // Publish
    id("maven-publish")


    id("scala")
    // JFX
    id("org.openjfx.javafxplugin") version ("0.0.10")

}
/*
var lib_version : String by rootProject.extra
println("V: $lib_version")
version = lib_version
group = "org.odfi.ioda"*/

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
  //  scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}

// Dependencies
//----------------------
javafx {
    version = "18-ea+2"
    modules(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media", "javafx.web", "javafx.swing")
}

val ooxooVersion: String by rootProject.extra
val indesignVersion: String by rootProject.extra
val ubrokerVersion: String by rootProject.extra

dependencies {

//    compile project(":fwapp")
    api (project(":ioda-core"))
    api (project(":ioda-instruments"))

    //api "com.kodedu.terminalfx:terminalfx:1.2.0-SNAPSHOT"
    api ("org.controlsfx:controlsfx:11.1.0")

    api ("net.mahdilamb:colormap:0.9.511")

    // https://mvnrepository.com/artifact/org.graalvm.js/js
    //api group: 'org.graalvm.js', name: 'js', version: '20.3.0'
    //api group: 'org.graalvm.js', name: 'js-scriptengine', version: '20.3.0'

    //api("org.scala-lang:scala-library:$scala_version")
   // api("org.scala-lang:scala3-library_3.0.0-M2:3.0.0-M2")

}
publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ioda-ui"
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
                password = System.getenv ("PUBLISH_PASSWORD")
            }
        }
    }
}

