dependencies {
    implementation(project(":nms:api"))
    implementation(project(":nms:v1_16R3"))

    shadow("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT")
    shadow("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    shadow("io.netty:netty-all:4.1.100.Final")
    shadow("com.mojang:authlib:3.2.38")
    shadow("it.unimi.dsi:fastutil:8.5.12")
    shadow("com.mojang:brigadier:1.0.500")
}