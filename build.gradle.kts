import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.constants.OBF_NAMESPACE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    kotlin("jvm") version "2.2.21" apply false
    id("idea")
    id("org.ajoberstar.grgit.service") version "5.3.0" apply false
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}

idea {
    project.jdkName = "21"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "5.0.0-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "application")
    apply(plugin = "com.gradleup.shadow")

    application {
        mainClass.set("net.dzikoysk.funnyguilds.FunnyGuilds")
    }

    repositories {
        /* Panda libs & Maven Central */
        maven("https://maven.reposilite.com/maven-central")
        maven("https://maven.reposilite.com/releases")
        maven("https://maven.reposilite.com/snapshots")
        
        /* Servers */
        maven("https://libraries.minecraft.net")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")

        /* 3rd party libs */
        maven("https://storehouse.okaeri.eu/repository/maven-public") {
            mavenContent {
                releasesOnly()
            }
        }
        maven("https://maven.kawusia.cloud/releases") {
            mavenContent {
                releasesOnly()
            }
        }
        maven("https://maven.kawusia.cloud/snapshots") {
            mavenContent {
                snapshotsOnly()
            }
        }
        maven("https://maven.reposilite.com/jitpack")

        /* Hooks */
        maven("https://maven.enginehub.org/repo")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
        maven("https://nexus.codecrafter47.de/content/repositories/public")
        maven("https://repo.codemc.io/repository/maven-public")
        maven("https://repo.viaversion.com/everything")
        maven("https://repo.mikeprimm.com")
    }
}

subprojects {
    dependencies {
        /* general */
        compileOnly("org.jetbrains:annotations:24.0.1")

        /* tests */
        val junit = "5.10.2"
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

        val mockito = "5.15.2"
        testImplementation("org.mockito:mockito-core:$mockito")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockito")

        testImplementation(kotlin("test"))
        testImplementation("nl.jqno.equalsverifier:equalsverifier:3.14")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") // mute warnings
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs = listOf("-Xjvm-default=all") // Generate default methods in interfaces by default
        }
    }

    tasks.withType<Test> {
        jvmArgs("-XX:+EnableDynamicAgentLoading") // I hate JDK team (https://github.com/mockito/mockito/issues/3037)
        useJUnitPlatform()
        setForkEvery(1)
        maxParallelForks = 4

        testLogging {
            events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}


project(":nms").subprojects {
    tasks.withType<Javadoc>().configureEach { 
        enabled = false
    }

    dependencies {
        implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    }

    apply(plugin = "io.papermc.paperweight.userdev")
    tasks.withType<RemapJar> {
        toNamespace = OBF_NAMESPACE
    }
}
