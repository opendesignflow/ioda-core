
pluginManagement {

    pluginManagement {
        plugins {
            id("org.odfi.ooxoo") version "5.0.0-SNAPSHOT"
            id("org.openjfx.javafxplugin") version "0.0.12"
            id("com.github.maiflai.scalatest") version "0.31"
        }
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "ODFI Releases"
            url = java.net.URI("https://repo.opendesignflow.org/maven/repository/internal/")
        }
        maven {
            name = "ODFI Snapshots"
            url = java.net.URI("https://repo.opendesignflow.org/maven/repository/snapshots/")
        }
    }


}


// Modules
//-----------------
rootProject.name  = "ioda"
include(":ioda-core")
include(":ioda-ui")
include(":ioda-instruments")

