pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "ODFI Releases"
            url = java.net.URI("https://www.opendesignflow.org/maven/repository/internal/")
        }
        maven {
            name = "ODFI Snapshots"
            url = java.net.URI("https://www.opendesignflow.org/maven/repository/snapshots/")
        }
    }


}

include(":gradle-ooxoo-plugin")
include(":ooxoo-core")
include(":ooxoo-generator")
include(":ooxoo-db")
