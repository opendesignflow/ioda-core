// Common Properties
//----------------

var scalaMajorVersion by extra("3")
var scalaMinorVersion by extra("2.1")
val scalaVersion by extra {
    "$scalaMajorVersion.$scalaMinorVersion"
}

var ooxooVersion by extra("5.0.3")
var indesignVersion by extra("3.0.2")
var ubrokerVersion by extra("3.0.1")
var javafxVersion by extra("20-ea+1")

// Project version
var lib_version by extra("3.0.1-SNAPSHOT")
var branch by extra { System.getenv("BRANCH_NAME") }

if (System.getenv().getOrDefault("BRANCH_NAME", "dev").contains("release")) {
    lib_version = lib_version.replace("-SNAPSHOT", "")
}
println("Version is $lib_version")

allprojects {

    // Name + version
    group = "org.odfi.ioda"
    version = lib_version

    val ooxooVersion : String by rootProject.extra

    configurations.all {
        resolutionStrategy {
            //force("jakarta.json:jakarta.json-api:1.1.6")
            //force("jakarta.json.bind:jakarta.json.bind-api:1.0.2")
            force("org.odfi.ooxoo:ooxoo-core:$ooxooVersion")
            force("org.odfi.ooxoo:ooxoo-db:$ooxooVersion")
        }
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
            url = uri("https://repo.opendesignflow.org/maven/repository/internal/")
        }
        maven {
            name = "ODFI Snapshots"
            url = uri("https://repo.opendesignflow.org/maven/repository/snapshots/")
        }
        maven {
            url = uri("https://repo.triplequote.com/libs-release/")
        }
        google()
    }

}