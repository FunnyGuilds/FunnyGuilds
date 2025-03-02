pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "FunnyGuilds"

include(
    ":plugin",
    ":nms",
    ":nms:api",
    ":nms:v1_8R3",
    ":nms:v1_9R2",
    ":nms:v1_10R1",
    ":nms:v1_11R1",
    ":nms:v1_12R1",
    ":nms:v1_13R2",
    ":nms:v1_14R1",
    ":nms:v1_15R1",
    ":nms:v1_16R3",
    ":nms:v1_18R2",
    ":nms:v1_19R3",
    ":nms:v1_20R1",
    ":nms:v1_20R2",
    ":nms:v1_20R3",
    ":nms:v1_20R5",
    ":nms:v1_21R1",
    ":nms:v1_21R4"
)

val isCiServer = System.getenv().containsKey("CI")
// Cache build artifacts, so expensive operations do not need to be re-computed
buildCache {
   local {
       isEnabled = !isCiServer
   }
}
