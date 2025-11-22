rootProject.name = "FunnyGuilds"

include(
    // Server API
    "funnyguilds-server-api",
    // Core
    "funnyguilds-core",
    // Engines
    "funnyguilds-targets",
    "funnyguilds-targets:fake-server",
    "funnyguilds-targets:spigot-server",
    "funnyguilds-targets:spigot-server:core",
    // Features
    "funnyguilds-regions",
    "funnyguilds-rank",
    // Integrations
    "funnyguilds-hooks",
    "funnyguilds-hooks:placeholderapi",
    // Tests
    "funnyguilds-tests",
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}