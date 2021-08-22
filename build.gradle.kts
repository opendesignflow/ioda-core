
// Common Properties
//----------------

var scala_major by extra("2.13")
var scala_minor by extra("6")
var lib_version by extra("1.1.2-SNAPSHOT")
var branch by extra {System.getenv("BRANCH_NAME")}

if (System.getenv().getOrDefault("BRANCH_NAME","dev")=="release") {
    lib_version = lib_version.replace("-SNAPSHOT","")
}

allprojects {

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

}