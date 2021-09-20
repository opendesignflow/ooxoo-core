plugins {
    id ("scala")

    // Publish
    id ("maven-publish")
    id ("java-library")

}


// Versions
//-----------------


// Sources
//---------------
sourceSets {
    main {
        scala {
            srcDir("src/main/java")
        }

        java {
            this.setSrcDirs(emptyList<File>())
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
    //scalaCompileOptions.additionalParameters = listOf("-Ytasty-reader")
}

dependencies {


    api (project(":ooxoo-core"))
    api ("org.hibernate:hibernate-core:5.5.7.Final")
    api ("com.h2database:h2:1.4.200")

    /*testImplementation "org.scala-lang.modules:scala-xml_2.13:2.0.0-M3"
    testImplementation 'org.scalatest:scalatest-funsuite_2.13:3.2.6'
    testImplementation 'org.scalatest:scalatest-shouldmatchers_2.13:3.2.6'*/
}



publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ooxoo-db"
            from(components["java"])

            pom {
                name.set("DB for OOXOO")
                description.set("Utilities for interfacing DB libraries with OOXOO")
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
            var releasesRepoUrl = uri("https://www.opendesignflow.org/maven/repository/internal/")
            var snapshotsRepoUrl = uri("https://www.opendesignflow.org/maven/repository/snapshots")

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