
// Common Properties
//----------------

var scala_major by extra("2.13")
var scala_minor by extra("6")
var lib_version by extra("1.1.2-SNAPSHOT")
var branch by extra {System.getenv("BRANCH_NAME")}

if (System.getenv().getOrDefault("BRANCH_NAME","dev")=="release") {
    lib_version = lib_version.replace("-SNAPSHOT","")
}