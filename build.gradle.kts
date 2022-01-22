// Versions
//-----------------
var scalaMajorVersion by extra("2.13")
var scalaMinorVersion by extra("7")
val scalaVersion by extra {
    "$scalaMajorVersion.$scalaMinorVersion"
}

// Project version
var lib_version by extra("4.9.1-SNAPSHOT")
var branch by extra { System.getenv("BRANCH_NAME") }
if (System.getenv().getOrDefault("BRANCH_NAME", "dev").contains("release")) {
    lib_version = lib_version.replace("-SNAPSHOT", "")
}

// Deps
val teaVersion by extra("5.0.0")

group = "org.odfi.ooxoo"
version = lib_version

tasks.create("checkForSnapshots") {
    doFirst {
        project.allprojects.forEach { project ->
            project.configurations.forEach { c ->
                c.dependencies.forEach { dep ->
                    val isSnapshot = dep.version?.endsWith("-SNAPSHOT") ?: false
                    if (isSnapshot) {
                        throw kotlin.RuntimeException("Snapshot Dependency detected: $dep")
                    }
                }
            }
        }
    }
}

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

