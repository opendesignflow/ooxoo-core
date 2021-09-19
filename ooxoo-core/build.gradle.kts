

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
    scalaCompileOptions.additionalParameters = listOf("-Ytasty-reader")
}


dependencies {


    var scalaMajorVersion by extra("3")

    // ODFI Deps
    //--------------
    api ("org.odfi:tea:4.0.0-SNAPSHOT")

    // Common JAXB Stuff
    //---------------

    // Special Buffer types like HTML string, compresssing buffer
    api ("org.apache.commons:commons-lang3:3.12.0")
    api ("org.apache.commons:commons-compress:1.21")

    // Stax indenting output
    api ("org.glassfish.jaxb:txw2:3.0.1")

    // Producer
    //--------------

    // Library to make words plurals
    api ("org.atteo:evo-inflector:1.3")


    // JSON API and Impl for javax
    //----------------

    api ("org.eclipse:yasson:1.0.9")
    //api 'javax.json:javax.json-api:2.0.0'
    //api "javax.json.bind:javax.json.bind-api:1.0.2"


    // GSON
    //---------------
    api ("com.google.code.gson:gson:2.8.8")


    // JPA Interfaces
    //-----------------
    api ("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")
    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
   // api group: 'javax.persistence', name: 'javax.persistence-api', version: '3.0.0'
    api( "javax.persistence:javax.persistence-api:2.2")

    // Scala
    //---------------
    //api ("org.scala-lang.modules:scala-parser-combinators_$scalaMajorVersion:2.0.0")
    /*api "org.scala-lang:scala-reflect:$scalaVersion"
    api "org.scala-lang:scala-compiler:$scalaVersion"
    api("org.scala-lang:scala-library:$scalaVersion")*/

   /* testImplementation "org.scala-lang.modules:scala-xml_2.13:2.0.0-M3"
    testImplementation 'org.scalatest:scalatest-funsuite_2.13:3.2.6'
    testImplementation 'org.scalatest:scalatest-shouldmatchers_2.13:3.2.6'*/
}


publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "ooxoo-core"
            from(components["java"])

            pom {
                name.set("OOXOO Core")
                description.set("Core runtime for OOXOO")
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