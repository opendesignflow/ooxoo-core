// Versions
//-----------------
var scalaMajorVersion by extra("3")
var scalaMinorVersion by extra("2.1")
val scalaVersion by extra {
    "$scalaMajorVersion.$scalaMinorVersion"
}

// Project version
var lib_version by extra("5.0.4-SNAPSHOT")
var branch by extra { System.getenv("BRANCH_NAME") }
if (System.getenv().getOrDefault("BRANCH_NAME", "dev").contains("release")) {
    lib_version = lib_version.replace("-SNAPSHOT", "")
}

// Deps
val teaVersion by extra("5.0.3")

group = "org.odfi.ooxoo"
version = lib_version

allprojects {

    // Name + version
    group = "org.odfi.ooxoo"
    version = lib_version



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

