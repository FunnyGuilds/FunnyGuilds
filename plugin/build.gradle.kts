import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("xyz.jpenilla.run-paper")
    id("org.ajoberstar.grgit.service")
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
            from(components["shadow"])

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

@Suppress("VulnerableLibrariesLocal")
dependencies {
    /* funnyguilds */
    project.project(":nms").subprojects.forEach {
        implementation(it)
    }
    implementation("net.dzikoysk:funnycommands:0.8.0")

    /* std */
    val expressible = "1.3.6"
    api("org.panda-lang:expressible:$expressible")
    testImplementation("org.panda-lang:expressible-junit:$expressible")

    /* okaeri config library */
    val okaeriConfigs = "6.0.0-beta.27"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")
    // okaeri holographicdisplays commons
    implementation("eu.okaeri:okaeri-commons-bukkit-holographicdisplays:0.2.27")

    val yamlVersion = "6.8.0-SNAPSHOT"
    implementation("dev.peri.yetanothermessageslibrary:core:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:repository-okaeri:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:platform-bukkit:$yamlVersion")

    implementation("me.pikamug.localelib:LocaleLib:4.1.3")

    /* general stuff */
    @Suppress("GradlePackageUpdate")
    implementation("com.zaxxer:HikariCP:4.0.3")

    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    
    implementation("org.bstats:bstats-bukkit:3.1.0")

    // probably fix for some exception?
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // bukkit stuff
    shadow("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    shadow("org.apache.logging.log4j:log4j-core:2.20.0")

    /* hooks */
    shadow("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    shadow("net.milkbowl.vault:VaultAPI:1.7")
    shadow("me.clip:placeholderapi:2.11.7")
    shadow("com.github.decentsoftware-eu:decentholograms:2.8.12")
    shadow("us.dynmap:DynmapCoreAPI:3.7-beta-6")

    /* tests */
    testImplementation("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    testImplementation("com.mojang:authlib:6.0.57")
}

tasks.processResources {
    val grgit = grgitService.service.get().grgit
    val isCiServer = System.getenv().containsKey("CI")
    
    val version = "${project.version}${if (isCiServer) "+CI" else ""}"
    val commitId = grgit.head().abbreviatedId
    
    expand(
        "funnyGuildsVersion" to version,
        "funnyGuildsCommit" to commitId
    )
}

tasks.withType<ShadowJar> {
    val commitCount = grgitService.service.get().grgit.log().size
    archiveFileName = "FunnyGuilds ${project.version}.$commitCount (MC 1.21.x).jar"

    relocate("net.dzikoysk.funnycommands", "net.dzikoysk.funnyguilds.libs.net.dzikoysk.funnycommands")
    relocate("panda.utilities", "net.dzikoysk.funnyguilds.libs.panda.utilities")
    relocate("javassist", "net.dzikoysk.funnyguilds.libs.javassist")
    relocate("com.zaxxer", "net.dzikoysk.funnyguilds.libs.com.zaxxer")
    relocate("org.apache.logging", "net.dzikoysk.funnyguilds.libs.org.apache.logging")
    relocate("org.slf4j", "net.dzikoysk.funnyguilds.libs.org.slf4j")
    relocate("org.bstats", "net.dzikoysk.funnyguilds.libs.bstats")
    relocate("eu.okaeri", "net.dzikoysk.funnyguilds.libs.eu.okaeri")
    relocate("dev.peri", "net.dzikoysk.funnyguilds.libs.dev.peri")
    relocate("me.pikamug", "net.dzikoysk.funnyguilds.libs.me.pikamug")
    relocate("org.mariadb", "net.dzikoysk.funnyguilds.libs.org.mariadb")

    exclude("org/checkerframework/**")
    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")
    exclude("com/google/errorprone/**")
    exclude("META-INF/services/javax.annotation.processing.Processor")

    minimize {
        exclude(dependency("net.dzikoysk:funnycommands:.*"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-core:.*"))
        exclude(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))

        // nms implementation modules are not referenced in the project but are required at runtime
        parent!!.project(":nms").subprojects.forEach {
            exclude(project(it.path))
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
}
