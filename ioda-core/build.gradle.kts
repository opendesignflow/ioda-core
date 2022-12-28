buildscript {
    dependencies {
        classpath("org.odfi.indesign:indesign-core:3.0.0-SNAPSHOT")
        // classpath ("org.odfi.indesign:indesign-core:$indesign_version")
        classpath("org.odfi.ubroker:ubroker-core:3.0.0-SNAPSHOT")
    }
}

plugins {

    // Scala
    id("scala")
    id("com.github.maiflai.scalatest")

    // OOXOO
    id("org.odfi.ooxoo")

    // Publish
    id("maven-publish")
    id("java-library")

}

//version = gradle.ext.has("version") ? gradle.ext.version : "dev"

ooxoo {
    javax.set(true)
}

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
    //scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}

// Dependencies
//-------------------
val scalaMajorVersion: String by rootProject.extra
val indesignVersion: String by rootProject.extra
val ubrokerVersion: String by rootProject.extra
val ooxooVersion: String by rootProject.extra



dependencies {


    // Json
    //-----------
    // https://mvnrepository.com/artifact/org.eclipse/yasson
    // https://mvnrepository.com/artifact/jakarta.json/jakarta.json-api
    //api("jakarta.json:jakarta.json-api:1.1.6]")

    implementation("org.eclipse:yasson:1.0.11")

    // ODFI
    //----------
    api("org.odfi.ooxoo:ooxoo-core:$ooxooVersion")
    api("org.odfi.ubroker:ubroker-core:$ubrokerVersion")
    api("org.odfi.indesign:indesign-core:$indesignVersion")


    // External dependencies
    //-------------
    /*val jacksonVersion = "2.13.1"
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")*/

    api("org.jmdns:jmdns:3.5.8")

    api("org.apache.logging.log4j:log4j-api:2.19.0")
    api("org.apache.logging.log4j:log4j-core:2.19.0")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api-scala
    // api("org.apache.logging.log4j:log4j-api-scala_2.13:12.0")

    //api("org.apache.logging.log4j:log4j-api-scala_$scalaMajor:12.0")
    api("org.fusesource.jansi:jansi:2.4.0")

    api("org.apache.httpcomponents:fluent-hc:4.5.14")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    // api group: 'com.google.code.gson', name: 'gson', version: '2.8.6'

    //api("org.scala-lang:scala-library:$scala_version")

    //implementation("javax.persistence:javax.persistence-api:3.0.0")
    // https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api
    // implementation("jakarta.persistence:jakarta.persistence-api:3.0.0")


    // Test
    //---------------------
    testImplementation("commons-io:commons-io:2.11.0")
    testImplementation("org.scalatest:scalatest-funsuite_3:3.2.14")
    testImplementation("org.scalatest:scalatest-shouldmatchers_3:3.2.14")
    testImplementation("com.vladsch.flexmark:flexmark-all:0.64.0")
    //testRuntimeOnly("org.eclipse:yasson:2.0.4")
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