plugins {
    `java-library`
    kotlin("jvm") version "2.1.10" apply false

    application
    `maven-publish`

    id("idea")
    id("org.ajoberstar.grgit.service") version "5.3.0" apply false
    id("com.gradleup.shadow") version "9.0.0-beta2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14" apply false
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0" apply false
    id("xyz.jpenilla.run-paper") version "2.2.4" apply false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "4.13.1-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "application")
    apply(plugin = "com.gradleup.shadow")

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.isFork = true
        options.compilerArgs.add("-Xlint:-options") // mute warnings about use of an obsolete version
    }

    application {
        mainClass = "net.dzikoysk.funnyguilds.FunnyGuilds" //TODO: Move somewhere elese
    }

    repositories {
        /* Libs */
        maven("https://maven.reposilite.com/maven-central")
        maven("https://maven.reposilite.com/releases")
        maven("https://maven.reposilite.com/snapshots")

        /* Servers */
        maven("https://libraries.minecraft.net")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

subprojects {
    dependencies {
        /* general */
        compileOnlyApi("org.jetbrains:annotations:26.0.2")
    }

    tasks.withType<Javadoc> {
        //options.encoding = "UTF-8"
        options {
            require(this is StandardJavadocDocletOptions)
            addStringOption("Xdoclint:none", "-quiet") // mute warnings
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
