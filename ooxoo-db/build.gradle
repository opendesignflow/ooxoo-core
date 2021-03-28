buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            name = 'Sonatype Nexus Snapshots'
            url = 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }
}

plugins {
    id 'scala'

    // Publish
    id 'maven-publish'
    id "java-library"

}

// Versions
//-----------------
group = 'org.odfi.ooxoo'
version = gradle.ext.has("version") ?  gradle.ext.version :  "dev"
def scalaVersion = "2.13.5"

// Sources
//---------------
sourceSets {
    main {
        scala {
            srcDirs = ['src/main/scala', 'src/main/java']
        }
        java {
            srcDirs = []
        }
    }
}

// Deps
//-----------
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
dependencies {


    api project(":core")
    api "org.hibernate:hibernate-core:5.2.10.Final"
    api "com.h2database:h2:1.4.197"

    testImplementation "org.scala-lang.modules:scala-xml_2.13:2.0.0-M3"
    testImplementation 'org.scalatest:scalatest-funsuite_2.13:3.2.6'
    testImplementation 'org.scalatest:scalatest-shouldmatchers_2.13:3.2.6'
}


repositories {

    mavenLocal()
    mavenCentral()
    maven {
        name = 'Sonatype Nexus Snapshots'
        url = 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
    maven {
        name = 'ODFI Releases'
        url = 'https://www.opendesignflow.org/maven/repository/internal/'
    }
    maven {
        name = 'ODFI Snapshots'
        url = 'https://www.opendesignflow.org/maven/repository/snapshots/'
    }
    google()
    jcenter()
}

publishing {
    publications {

        publishToMavenLocal(MavenPublication) {

    
            artifactId "ooxoo-db"
            from components.java
            
            pom {
                name = 'OOXOO DB'
                description = 'DB for OOXOO'

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
            //-------------
            def mavenOdfiPropertiesFile = new File(System.getProperty("user.home") + File.separator + ".gradle/odfi-maven.properties").getCanonicalFile()

            if (mavenOdfiPropertiesFile.exists()) {
                def keystoreProperties = new Properties()
                keystoreProperties.load(new FileInputStream(mavenOdfiPropertiesFile))

                credentials {
                    username keystoreProperties["user"]
                    password keystoreProperties["password"]
                }

            } else {
                credentials {
                    username System.getenv("PUBLISH_USERNAME")
                    password System.getenv("PUBLISH_PASSWORD")
                }
            }
        }
    }
}