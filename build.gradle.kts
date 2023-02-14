import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"

    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    id("idea")

    id("xyz.jpenilla.run-paper") version "2.0.1"
}

idea {
    project.jdkName = "17"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "5.0.0"

    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "application")
    apply(plugin = "com.github.johnrengelman.shadow")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

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
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            languageVersion = "1.8"
            freeCompilerArgs = listOf(
                "-Xjvm-default=all", // For generating default methods in interfaces
                "-Xcontext-receivers"
            )
        }
    }
}

subprojects {
    tasks.test {
        useJUnitPlatform()
    }
}