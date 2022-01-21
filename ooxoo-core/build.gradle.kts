val teaVersion: String by rootProject.extra
var scalaMajorVersion: String by rootProject.extra


plugins {



    // Publish
    id("maven-publish")
    id("java-library")

    id("scala")
    id("com.github.maiflai.scalatest") version "0.31"

}

// Versions
//-----------------

// Sources
//---------------
sourceSets {
    main {
        scala {
            srcDir("src/main/java")
            srcDir("src/main/scala")
        }

        java {
            this.setSrcDirs(emptyList<File>())
            //srcDir("src/main/java")
        }
    }
}

// Deps
//-----------
java {
    /*targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11*/
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
    withJavadocJar()
//    withSourcesJar()
}



tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

// Scala compilation options
tasks.withType<ScalaCompile>().configureEach {
    this.targetCompatibility = "11"
    //this.scalaCompileOptions.
    //scalaCompileOptions.ta
    //scalaCompileOptions.additionalParameters = listOf( "-target:11", "-Xtarget:11")
    //scalaCompileOptions.additionalParameters = listOf("-rewrite", "-source", "3.0-migration")
}


dependencies {


    // ODFI Deps
    //--------------
    api("org.odfi:tea_$scalaMajorVersion:$teaVersion")
    //api("org.odfi:tea_3:$teaVersion")

    // Common JAXB Stuff
    //---------------

    // Special Buffer types like HTML string, compresssing buffer
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.apache.commons:commons-compress:1.21")

    // Stax indenting output
    api("org.glassfish.jaxb:txw2:3.0.1")

    // Producer
    //--------------

    // Library to make words plurals
    api("org.atteo:evo-inflector:1.3")


    // JSON API and Impl for javax
    //----------------
    //api("org.eclipse:yasson:1.0.9")
    //api("org.glassfish:javax.json:1.1.4")
    api("jakarta.json:jakarta.json-api:1.1.6")
    api("jakarta.json.bind:jakarta.json.bind-api:1.0.2")



    val jacksonVersion = "2.13.1"
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")


    // GSON
    //---------------
    api("com.google.code.gson:gson:2.8.9")



    // JPA/javax Interfaces
    //-----------------
    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
   // api("javax.persistence:javax.persistence-api:3.0.0")
    api("javax.persistence:javax.persistence-api:2.2")
    // api("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final")

    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
    // api group: 'javax.persistence', name: 'javax.persistence-api', version: '3.0.0'
   // api("javax.persistence:javax.persistence-api:2.2")

    // Scala Tests
    //---------------
    //api ("org.scala-lang.modules:scala-parser-combinators_$scalaMajorVersion:2.0.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.scalatest:scalatest-funsuite_$scalaMajorVersion:3.2.10")
    testImplementation("org.scalatest:scalatest-shouldmatchers_$scalaMajorVersion:3.2.10")
    testImplementation("com.vladsch.flexmark:flexmark-all:0.62.2")
    testRuntimeOnly("org.eclipse:yasson:1.0.8")
// https://mvnrepository.com/artifact/org.glassfish/javax.json
    //testRuntimeOnly("org.glassfish:javax.json:1.1.4")

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