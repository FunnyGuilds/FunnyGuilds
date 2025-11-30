rootProject.name = "FunnyGuilds"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

include(
    ":plugin",
    ":nms:api",
    ":nms:v1_21",
    ":nms:v1_21_4",
)

val isCiServer = System.getenv().containsKey("CI")
// Cache build artifacts, so expensive operations do not need to be re-computed
buildCache {
   local {
       isEnabled = !isCiServer
   }
}
