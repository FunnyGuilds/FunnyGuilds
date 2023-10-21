import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    id("idea")
    id("org.ajoberstar.grgit") version "4.1.1"
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.0"
}

idea {
    project.jdkName = "17"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "5.0.0-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply(plugin = "application")
    apply(plugin = "com.github.johnrengelman.shadow")

    application {
        mainClass.set("net.dzikoysk.funnyguilds.FunnyGuilds")
    }

    repositories {
        /* Libs */
        mavenCentral()
        maven("https://maven.reposilite.com/releases")
        maven("https://maven.reposilite.com/jitpack")
        maven("https://storehouse.okaeri.eu/repository/maven-public")
        maven("https://repo.titanvale.net/releases")

        /* Servers */
        maven("https://libraries.minecraft.net")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")

        /* Hooks */
        maven("https://maven.enginehub.org/repo")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
        maven("https://nexus.codecrafter47.de/content/repositories/public")
        maven("https://repo.codemc.io/repository/maven-public")
        maven("https://repo.viaversion.com")
    }
}

subprojects {
    dependencies {
        /* general */

        compileOnly("org.jetbrains:annotations:24.0.1")
        testImplementation(kotlin("stdlib-jdk8"))

        /* tests */

        val junit = "5.10.0"
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

        val mockito = "5.6.0"
        testImplementation("org.mockito:mockito-core:$mockito")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockito")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.10")
        testImplementation("nl.jqno.equalsverifier:equalsverifier:3.15.2")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).let {
            it.addStringOption("Xdoclint:none", "-quiet") // mute warnings
            it.links(
                "https://spigotdocs.okaeri.cloud/1.16.5/",
                "https://javadoc.io/doc/org.panda-lang/expressible/1.3.4/",
            )
            it.encoding = "UTF-8"
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_16.toString()
            languageVersion = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all") // Generate default methods in interfaces by default
        }
    }

    publishing {
        repositories {
            maven {
                name = "reposilite"
                url = uri("https://maven.reposilite.com/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")
                credentials {
                    username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
                    password = System.getenv("MAVEN_TOKEN") ?: property("mavenPassword").toString()
                }
            }
        }
        publications {
            create<MavenPublication>("library") {
                artifact(tasks["shadowJar"]) {
                    classifier = ""
                }
                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])

                // Add external repositories to published artifacts
                // ~ btw: pls don't touch this
                pom.withXml {
                    val repositories = asNode().appendNode("repositories")
                    project.repositories.findAll(closureOf<Any> {
                        if (this is MavenArtifactRepository && this.url.toString().startsWith("https")) {
                            val repository = repositories.appendNode("repository")
                            repository.appendNode("id", this.url.toString().replace("https://", "").replace("/", "-").replace(".", "-").trim())
                            repository.appendNode("url", this.url.toString().trim())
                        }
                    })
                }
            }
        }
    }

    tasks.withType<Test> {
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
