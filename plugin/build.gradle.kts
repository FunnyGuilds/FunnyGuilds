import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    kotlin("jvm")
    id("org.ajoberstar.grgit.service")
    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("xyz.jpenilla.run-paper")
}

val pluginPackage = "net.dzikoysk.funnyguilds"
val libsPackage = "$pluginPackage.libs"
val pluginMain = "$pluginPackage.FunnyGuilds"

bukkitPluginYaml {
    val grgit = grgitService.service.get().grgit
    val isCiServer = System.getenv().containsKey("CI")
    
    val projectVersion = "${project.version}${if (isCiServer) "+CI" else ""}"
    val commitId = grgit.head().abbreviatedId
    
    main = pluginMain
    version = "$projectVersion Snowdrop-$commitId"
    author = "FunnyGuilds Team"
    website = "https://github.com/FunnyGuilds"
    softDepend = listOf(
        "WorldEdit",
        "WorldGuard",
        "Vault",
        "PlaceholderAPI",
        "FunnyTab",
        "HolographicDisplays",
        "DecentHolograms",
        "Multiverse-Core",
        "dynmap"
    )
    apiVersion = "1.13"

    permissions {
        register("funnyguilds.*") {
            default = Permission.Default.OP
            children(
                "funnyguilds.player",
                "funnyguilds.vip",
                "funnyguilds.admin"
            )
        }
        register("funnyguilds.admin") {
            default = Permission.Default.OP
            children(
                "funnyguilds.reload",
                "funnyguilds.admin.build",
                "funnyguilds.admin.interact",
                "funnyguilds.admin.teleport",
                "funnyguilds.admin.notification",
            )
            children.put(
                "funnyguilds.admin.disabledummy",
                false
            )
        }
        register("funnyguilds.vip") {
            default = Permission.Default.OP
            children(
                "funnyguilds.vip.items",
                "funnyguilds.vip.rank",
                "funnyguilds.vip.base",
                "funnyguilds.vip.baseTeleportTime"
            )
        }
        register("funnyguilds.player") {
            default = Permission.Default.TRUE
            children(
                "funnyguilds.ally",
                "funnyguilds.base",
                "funnyguilds.break",
                "funnyguilds.create",
                "funnyguilds.delete",
                "funnyguilds.deputy",
                "funnyguilds.enlarge",
                "funnyguilds.escape",
                "funnyguilds.guild",
                "funnyguilds.info",
                "funnyguilds.invite",
                "funnyguilds.items",
                "funnyguilds.join",
                "funnyguilds.kick",
                "funnyguilds.leader",
                "funnyguilds.leave",
                "funnyguilds.playerinfo",
                "funnyguilds.pvp",
                "funnyguilds.ranking",
                "funnyguilds.rankreset",
                "funnyguilds.statsreset",
                "funnyguilds.setbase",
                "funnyguilds.tnt",
                "funnyguilds.top",
                "funnyguilds.validity",
                "funnyguilds.war"
            )
        }
    }
}

repositories {
    /* Libs */
    maven("https://storehouse.okaeri.eu/repository/maven-public") {
        mavenContent {
            releasesOnly()
        }
    }
    maven("https://repo.titanvale.net/releases") {
        mavenContent {
            releasesOnly()
        }
    }
    maven("https://repo.titanvale.net/snapshots") {
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
    maven("https://repo.mikeprimm.com")
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    /* funnyguilds */

    rootProject.project(":nms").subprojects.forEach {
        implementation(project(":${it.path}", configuration = "shadow"))
    }

    implementation("net.dzikoysk:funnycommands:0.8.0")

    /* std */

    val expressible = "1.3.6"
    api("org.panda-lang:expressible:$expressible")
    testImplementation("org.panda-lang:expressible-junit:$expressible")

    /* okaeri config library */

    val okaeriConfigs = "5.0.5"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")
    // okaeri holographicdisplays commons
    implementation("eu.okaeri:okaeri-commons-bukkit-holographicdisplays:0.2.27")

    /* messages libraries */

    val adventureVersion = "4.18.0"
    implementation("net.kyori:adventure-api:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4") // adventure-platform has other versioning than adventure-api

    val yamlVersion = "6.8.0-SNAPSHOT"
    implementation("dev.peri.yetanothermessageslibrary:core:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:repository-okaeri:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:platform-bukkit:$yamlVersion")

    implementation("com.github.PikaMug:LocaleLib:4.1.0")

    /* general stuff */

    @Suppress("GradlePackageUpdate")
    implementation("com.zaxxer:HikariCP:4.0.3")

    @Suppress("GradlePackageUpdate")
    implementation("com.google.guava:guava:21.0") {
        because("WorldEdit defined a constraint that we must use 21.0 and there is no way to ignore it")
    }

    @Suppress("GradlePackageUpdate")
    implementation("com.google.code.gson:gson:2.8.0") {
        because("WorldEdit defined a constraint that we must use 2.8.0 and there is no way to ignore it")
    }

    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // probably fix for some exception?
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // bukkit stuff
    shadow("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    shadow("org.apache.logging.log4j:log4j-core:2.20.0")

    /* hooks */

    shadow("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    shadow("net.milkbowl.vault:VaultAPI:1.7")
    shadow("me.clip:placeholderapi:2.11.6") {
        //because("PlaceholderAPI on versions higher than 2.10.9 causes GH-1700 for some unknown reason")
        exclude(
            group = "com.google.code.gson",
            module = "gson"
        )
    }
    shadow("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    shadow("com.github.decentsoftware-eu:decentholograms:2.8.12")
    shadow("us.dynmap:DynmapCoreAPI:3.6")

    /* tests */
    testImplementation(kotlin("test"))

    val junit = "5.10.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

    val mockito = "5.12.0"
    testImplementation("org.mockito:mockito-core:$mockito")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockito")

    testImplementation(kotlin("test"))
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.14")

    testImplementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.mojang:authlib:3.2.38")
}

tasks.compileJava {
    options.release = 8
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        // Generate default methods in interfaces by default
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

tasks.withType<ShadowJar> {
    val commitCount = grgitService.service.get().grgit.log().size
    archiveFileName = "FunnyGuilds ${project.version}.$commitCount (MC 1.8-1.21).jar"

    setOf(
        "net.dzikoysk.funnycommands",
        "panda.utilities",
        "org.panda_lang.utilities.inject",
        "javassist",
        "com.zaxxer",
        "org.apache.commons.lang3",
        "org.apache.logging",
        "org.slf4j",
        "org.bstats",
        "eu.okaeri",
        "net.kyori",
        "dev.peri",
        "me.pikamug",
        "org.mariadb",
        "com.sun.jna",
        "com.github.benmanes",
        "waffle"
    ).forEach {
        relocate(
            it,
            "$libsPackage.$it"
        )
    }

    relocate("com.google", "$libsPackage.com.google") {
        exclude("com.google.gson.**")
    }

    /* exclusions */

    setOf(
        "org/checkerframework/**",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/services/javax.annotation.processing.Processor"
    ).forEach { exclude(it) }

    minimize {
        exclude(dependency("net.dzikoysk:funnycommands:.*"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-core:.*"))
        exclude(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))

        // nms implementation modules are not referenced in the project but are required at runtime
        rootProject.project(":nms").subprojects.forEach {
            exclude(project(it.path))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    /**
     * https://github.com/mockito/mockito/issues/3037
     * https://github.com/mockito/mockito/issues/3111
     */
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off"
    )

    setForkEvery(1)
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    testLogging {
        events(
            TestLogEvent.STARTED,
            TestLogEvent.PASSED,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}

publishing {
    repositories {
        maven {
            name = "reposilite"
            url = uri(
                "https://maven.reposilite.com/${
                    if (version.toString().endsWith("-SNAPSHOT")) "snapshots"
                    else "releases"
                }"
            )
            credentials {
                username = System.getenv("MAVEN_NAME")
                    ?: property("mavenUser").toString()
                password = System.getenv("MAVEN_TOKEN")
                    ?: property("mavenPassword").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["shadow"])
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

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

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
}
