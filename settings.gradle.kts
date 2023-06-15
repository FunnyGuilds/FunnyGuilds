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
    "funnyguilds-targets:spigot-server:nms",
    "funnyguilds-targets:spigot-server:nms:api",
    "funnyguilds-targets:spigot-server:nms:v1_19R2",
    // Features
    "funnyguilds-regions",
    "funnyguilds-rank",
    // Integrations
    "funnyguilds-hooks",
    "funnyguilds-hooks:placeholderapi",
    // Tests
    "funnyguilds-tests",
)
