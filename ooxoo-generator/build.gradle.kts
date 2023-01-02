val teaVersion: String by rootProject.extra
var scalaMajorVersion: String by rootProject.extra

plugins {

    id("scala")
    id("com.github.maiflai.scalatest") version "0.31"

    // Publish
    id("application")
    id("maven-publish")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

// Shadow
//-----------------
application {
    mainClass.set("org.odfi.oooxoo.generator.OGenerator")
    //mainClass = "com.hyprint.qa.inline.testing.QATesting"
}
tasks.withType<Jar> {
    manifest {

        this.attributes(
            mapOf( "Main-Class" to application.mainClass.get() )
        )
    }
}


// Deps
//-----------
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    // withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

dependencies {


    // ODFI Deps
    //--------------
    api(project(":ooxoo-core"))
    implementation("commons-cli:commons-cli:1.5.0")
    api("org.odfi:tea-compiler_3:$teaVersion")

}


publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ooxoo-generator"
            from(components["java"])

            pom {
                name.set("OOXOO Generator")
                description.set("Genererator compiler interface to call the model generators")
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