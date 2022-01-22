val teaVersion: String by rootProject.extra
var scalaMajorVersion: String by rootProject.extra

plugins {

    id("scala")
    id("com.github.maiflai.scalatest") version "0.31"

    // Publish
    id("maven-publish")
    id("java-library")

}

// Versions
//-----------------

// Sources
//---------------
sourceSets {
    main {
        scala {

        }

    }
}

// Deps
//-----------
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
    // withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

// Scala compilation options
tasks.withType<ScalaCompile>().configureEach {
    // scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}


dependencies {


    // ODFI Deps
    //--------------
    api(project(":ooxoo-core"))
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