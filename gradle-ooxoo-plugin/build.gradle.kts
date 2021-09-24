/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details take a look at the Writing Custom Plugins chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.7/userguide/custom_plugins.html
 */

val kotlin_version: String by extra("1.5.30")

plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    id("java-gradle-plugin")


    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.5.30"

    // Publish
    id("maven-publish")
    id("com.gradle.plugin-publish") version ("0.13.0")
}
pluginBundle {
    website = "https://github.com/opendesignflow/ooxoo-core"
    vcsUrl = "https://github.com/opendesignflow/ooxoo-core"

    //tags = ["xml", "scala", "json", "jsonb", "generator", "marshall", "unmarshall"]
}

/*
group = 'org.odfi.ooxoo'
version = gradle.ext.has("version") ? gradle.ext.version : "dev"*/


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

dependencies {

    //implementation "$group:ooxoo-core:$version"
    implementation(project(":ooxoo-generator"))

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")


    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

gradlePlugin {
    plugins {
        this.create("ooxooPlugin").apply {
            id = "org.odfi.ooxoo"
            implementationClass = "org.odfi.ooxoo.gradle.plugin.OoxooGradlePluginPlugin"
            displayName = "OOXOO Gradle Plugin"
            description = "Plugin to generate Scala/JsonB Models from Scala Data Model DSL"
        }
    }
}

/*
tasks.getAt("compileScala").dependsOn.remove("compileJava")
tasks.getAt("compileKotlin").dependsOn("compileScala")
tasks.withType<ScalaCompile>().configureEach {
    this.ja
}*/
/*
gradlePlugin {
    // Define the plugin
    plugins {
        ooxooPlugin {
            id = "org.odfi.ooxoo"
            implementationClass = "org.odfi.ooxoo.gradle.plugin.OoxooGradlePluginPlugin"
            displayName = "OOXOO Gradle Plugin"
            description = "Plugin to generate Scala/JsonB Models from Scala Data Model DSL"
        }


    }
}*/

// Add a source set for the functional test suite
/*sourceSets {
    functionalTest {
    }
}
gradlePlugin.testSourceSets(sourceSets.functionalTest)
configurations.functionalTestImplementation.extendsFrom(configurations.testImplementation)*/

// Add a task to run the functional tests
/*tasks.register('functionalTest', Test) {
    testClassesDirs = sourceSets.functionalTest.output.classesDirs
    classpath = sourceSets.functionalTest.runtimeClasspath
}

tasks.named('check') {
    // Run the functional tests as part of `check`
    dependsOn(tasks.functionalTest)
}*/



publishing {
    publications {

        create<MavenPublication>("maven") {
            artifactId = "gradle-ooxoo-plugin"
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("OOXOO Gradle Plugin")
                description.set("Plugin to run OOXOO generator")
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

/*
publishing {
    publications {

        pluginPublication(MavenPublication) {

            groupId group
            artifactId "ooxoo-gradle-plugin"
            version version
            from components.java

            versionMapping {
                usage('java-api') {
                    fromResolutionOf('runtimeClasspath')
                }
                usage('java-runtime') {
                    fromResolutionResult()
                }
            }
            pom {
                name = 'OOXOO Gradle Plugin'
                description = 'Plugin to run OOXOO generator'

                //properties = [ ]
                licenses {
                    license {
                        name = 'The Apache License, Version 2.0'
                        url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                developers {
                    developer {
                        id = 'richnou'
                        name = 'Richnou'
                        email = 'leys.richard@gmail.com'
                    }
                }

            }
        }

    }
    repositories {
        maven {

            // change URLs to point to your repos, e.g. http://my.org/repo
            def releasesRepoUrl = "https://www.opendesignflow.org/maven/repository/internal/"
            def snapshotsRepoUrl = "https://www.opendesignflow.org/maven/repository/snapshots"
            url = version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl

            // Credentials
            //def mavenOdfiPropertiesFile = new File(System.getProperty("user.home") + File.separator + ".gradle/odfi-maven.properties").getCanonicalFile()

            credentials {
                username System.getenv("PUBLISH_USERNAME")
                password System.getenv("PUBLISH_PASSWORD")
            }
        }
    }
}*/
