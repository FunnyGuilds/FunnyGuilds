import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`
    kotlin("jvm") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.gradleup.shadow") version "9.2.2"
    id("idea")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

idea {
    project.jdkName = "21"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "6.0.0"

    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "application")
    apply(plugin = "com.gradleup.shadow")

    application {
        mainClass.set("net.dzikoysk.funnyguilds.FunnyGuilds")
    }

    repositories {
        /* Libs */
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.reposilite.com/releases")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<Javadoc> {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") // mute warnings
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_21.toString()
            languageVersion = "2.1"
            freeCompilerArgs = listOf(
                "-Xjvm-default=all", // For generating default methods in interfaces
                "-Xcontext-receivers"
            )
            javaParameters = true
        }
    }
}

subprojects {
    tasks.test {
        useJUnitPlatform()
    }
}