
val javafxVersion: String by rootProject.extra
val indesignVersion: String by rootProject.extra
val ubrokerVersion: String by rootProject.extra
val scalaMajorVersion: String by rootProject.extra

plugins {
    id("scala")
    id("java-library")

    // OOXOO
    id("org.odfi.ooxoo")

    // Publish
    id("maven-publish")

    // JFX
    id("org.openjfx.javafxplugin") version ("0.0.10")

    id("org.odfi.anarres.jnaerator") version ("1.0.1")

}
ooxoo {
    javax.set(true)
}

// Sources
//-------------------
tasks.jnaerator {
    libraryName = "visa"
    packageName = "org.odfi.ioda.instruments.nivisa"
    runtimeMode = com.ochafik.lang.jnaerator.JNAeratorConfig.Runtime.BridJ
    headerFiles(
        "src/main/jnaerator/fixes.h",
        "src/main/jnaerator/external/visa.h",
        "src/main/jnaerator/external/visatype.h",
        "src/main/jnaerator/external/vpptype.h"
    )

}

sourceSets {
    main {
        scala {
            // Generated from ooxoo
            srcDir(File(buildDir, "generated-sources/scala"))
            srcDir(File(buildDir, "generated-sources/jnaerator"))
        }
    }
    main {
        // No sources in java folder so that only scala phase compiles
        java.srcDirs.clear()
        java {

        }
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    //withSourcesJar()
}
tasks.jar.configure {
    this.duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
// Scala compilation options
tasks.withType<ScalaCompile>().configureEach {
    //scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}


// Dependencies
//----------------------
javafx {
    version = javafxVersion
    modules(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media", "javafx.web", "javafx.swing"
    )
}


dependencies {

//    compile project(":fwapp")
    api(project(":ioda-core"))
    api("org.odfi.indesign:indesign-stdplatform:$indesignVersion")

    // Dependencies
    //-------------------
    api("org.apache.commons:commons-compress:1.21")
    api("com.nativelibs4java:bridj:0.7.0")
    api("org.jfree:jfreechart:1.5.3")
    api("org.jfree:jfreesvg:3.4.3")
    api("org.jfree:jcommon:1.0.24")

    api("net.java.dev.jna:jna:4.2.0")
    api("org.apache.poi:poi:5.2.2")
    api("org.apache.poi:poi-ooxml:5.2.2")
   //api("org.apache.jackrabbit:jackrabbit-webdav:2.21.9")


    //-- Serial
    api("com.fazecast:jSerialComm:2.9.3")
    api("org.scream3r:jssc:2.8.0")
    api("dk.thibaut:jserial:1.0.3")

    api("org.scala-lang.modules:scala-parallel-collections_$scalaMajorVersion:1.0.4")
    //api("org.scala-lang:scala-library:$scala_version")
    testImplementation("org.scala-lang.modules:scala-xml_3:2.1.0")
    testImplementation("org.scalatest:scalatest-funsuite_3:3.2.14")
    testImplementation("org.scalatest:scalatest-shouldmatchers_3:3.2.14")
    //testImplementation("com.vladsch.flexmark:flexmark-all:0.35.10")

}
publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ioda-instruments"
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
            var releasesRepoUrl = uri("https://repo.opendesignflow.org/maven/repository/internal/")
            var snapshotsRepoUrl = uri("https://repo.opendesignflow.org/maven/repository/snapshots")

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

