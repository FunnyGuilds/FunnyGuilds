import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("xyz.jpenilla.run-paper")
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    /* funnyguilds */

    project(":nms").dependencyProject.subprojects.forEach {
        implementation(it)
    }
    implementation("net.dzikoysk:funnycommands:0.6.0")

    /* std */

    val expressible = "1.3.1"
    api("org.panda-lang:expressible:$expressible")
    testImplementation("org.panda-lang:expressible-junit:$expressible")

    /* okaeri config library */

    val okaeriConfigs = "4.0.9"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")
    // okaeri holographicdisplays commons
    implementation("eu.okaeri:okaeri-commons-bukkit-holographicdisplays:0.2.21")

    /* general stuff */

    @Suppress("GradlePackageUpdate")
    implementation("com.zaxxer:HikariCP:4.0.3")

    @Suppress("GradlePackageUpdate")
    implementation("com.google.guava:guava:21.0") {
        because("WorldEdit defined a constraint that we must use 21.0 and there is no way to ignore it")
    }

    @Suppress("GradlePackageUpdate")
    implementation("com.google.code.gson:gson:2.10.1") {
        because("WorldEdit defined a constraint that we must use 2.8.0 and there is no way to ignore it")
    }

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")

    // probably fix for some exception?
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    // bukkit stuff
    shadow("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    shadow("org.apache.logging.log4j:log4j-core:2.19.0")

    /* hooks */

    shadow("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    shadow("net.milkbowl.vault:VaultAPI:1.7")
    shadow("codecrafter47.bungeetablistplus:bungeetablistplus-api-bukkit:3.5.2")
    shadow("me.clip:placeholderapi:2.10.9") {
        because("PlaceholderAPI on versions higher than 2.10.9 causes GH-1700 for some unknown reason")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    shadow("net.kyori:adventure-api:4.12.0")
    shadow("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    shadow("com.github.decentsoftware-eu:decentholograms:2.7.9")

    /* tests */

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.8.0")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.12.3")
    testImplementation("org.spigotmc:spigot-api:1.16.2-R0.1-SNAPSHOT")
    testImplementation("com.mojang:authlib:3.2.38")

    val mockito = "4.11.0"
    testImplementation("org.mockito:mockito-core:$mockito")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockito")
    testImplementation("org.mockito:mockito-inline:$mockito")

    val junit = "5.9.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
}

tasks.processResources {
    expand(
        "funnyGuildsVersion" to version,
        "funnyGuildsCommit" to grgit.head().abbreviatedId
    )
}

tasks.withType<ShadowJar> {
    archiveFileName.set("FunnyGuilds ${project.version}.${grgit.log().size} (MC 1.8-1.19).jar")
    mergeServiceFiles()

    relocate("net.dzikoysk.funnycommands", "net.dzikoysk.funnyguilds.libs.net.dzikoysk.funnycommands")
    relocate("panda.utilities", "net.dzikoysk.funnyguilds.libs.panda.utilities")
    relocate("javassist", "net.dzikoysk.funnyguilds.libs.javassist")
    relocate("com.zaxxer", "net.dzikoysk.funnyguilds.libs.com.zaxxer")
    relocate("com.google", "net.dzikoysk.funnyguilds.libs.com.google") {
        exclude("com.google.gson.**")
    }
    relocate("org.apache.commons.lang3", "net.dzikoysk.funnyguilds.libs.org.apache.commons.lang3")
    relocate("org.apache.logging", "net.dzikoysk.funnyguilds.libs.org.apache.logging")
    relocate("org.slf4j", "net.dzikoysk.funnyguilds.libs.org.slf4j")
    relocate("org.bstats", "net.dzikoysk.funnyguilds.libs.bstats")
    relocate("eu.okaeri", "net.dzikoysk.funnyguilds.libs.eu.okaeri")

    exclude("org/checkerframework/**")
    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")

    minimize {
        exclude(dependency("net.dzikoysk:funnycommands:.*"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-core:.*"))
    }
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
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
