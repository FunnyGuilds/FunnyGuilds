dependencies {
    implementation(project(":nms:api"))
    implementation(project(":nms:v1_8R3"))

    shadow("org.spigotmc:spigot:1.20.4-R0.1-SNAPSHOT")
    shadow("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    shadow("org.apache.commons:commons-lang3:3.13.0")
    shadow("io.netty:netty-all:4.1.97.Final")
    shadow("it.unimi.dsi:fastutil:8.5.12")
    shadow("com.mojang:authlib:3.2.38")
    shadow("com.mojang:brigadier:1.2.9")
}