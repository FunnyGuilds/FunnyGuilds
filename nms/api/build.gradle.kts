repositories {
    maven("https://repo.viaversion.com")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    compileOnlyApi("com.mojang:authlib:3.2.38")
    compileOnlyApi("io.netty:netty-transport:4.1.108.Final")

    compileOnly("com.viaversion:viaversion-api:[4.0.0,5.0.0)")
}

