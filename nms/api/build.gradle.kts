dependencies {
    // The api module only uses the Bukkit/Adventure API surface (no net.minecraft), so it compiles
    // against paper-api directly rather than a Mojang-mapped dev bundle - no paperweight needed.
    // netty (channel handlers) and authlib (skin Property) are provided by the server at runtime but
    // are not exposed transitively by paper-api, so they are added here as compile-only deps.
    shadow("io.papermc.paper:paper-api:26.1.2.build.72-stable")
    shadow("io.netty:netty-transport:4.1.115.Final")
    shadow("com.mojang:authlib:6.0.57")
    shadow("com.viaversion:viaversion-api:5.6.0")
}
