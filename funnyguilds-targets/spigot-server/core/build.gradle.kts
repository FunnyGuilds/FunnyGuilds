plugins {
    id("xyz.jpenilla.run-paper")
}

dependencies {
    api(project(":funnyguilds-core"))
    project(":funnyguilds-targets:spigot-server:nms").dependencyProject.subprojects.forEach { implementation(it) }

    @Suppress("GradlePackageUpdate")
    fun locked() {
        implementation("com.zaxxer:HikariCP:4.0.3") { because("This major version works with current Java version") }
        implementation("com.google.guava:guava:21.0") { because("WorldEdit defined a constraint that we must use 21.0 and there is no way to ignore it") }
        implementation("com.google.code.gson:gson:2.8.0") { because("WorldEdit defined a constraint that we must use 2.8.0 and there is no way to ignore it") }

        compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
        shadow("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
        shadow("org.apache.logging.log4j:log4j-core:2.19.0")
        implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")
        implementation("org.apache.commons:commons-lang3:3.12.0")
    }
    locked()

    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("com.h2database:h2:2.1.214")
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}