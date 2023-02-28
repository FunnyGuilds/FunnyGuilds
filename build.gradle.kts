plugins {
    `java-library`
    application
    `maven-publish`

    id("idea")
    id("org.ajoberstar.grgit") version "4.1.1"
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

idea {
    project.jdkName = "17"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "4.11.1-SNAPSHOT"

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
        maven("https://maven.reposilite.com/releases")
        maven("https://storehouse.okaeri.eu/repository/maven-public")

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
        maven("https://jitpack.io")
        maven("https://repo.viaversion.com")
    }
}

subprojects {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") // mute warnings
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
                from(components.getByName("java"))

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
}