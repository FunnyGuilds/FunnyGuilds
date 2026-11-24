dependencies {
    api(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    // plugin classes are provided at runtime by the bundling plugin jar
    compileOnly(project(":plugin"))
    compileOnly("com.github.SkriptLang:Skript:2.17.1") {
        isTransitive = false
    }
    // supertypes of FunnyGuilds config classes referenced by config expressions
    compileOnly("eu.okaeri:okaeri-configs-yaml-bukkit:6.0.0-beta.27")
    compileOnly("eu.okaeri:okaeri-configs-serdes-commons:6.0.0-beta.27")
}
