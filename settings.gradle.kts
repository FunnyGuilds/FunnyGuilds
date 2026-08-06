rootProject.name = "FunnyGuilds"

// Limit workers - apologies to anyone heating their room with Gradle (4 -> 2, 8 -> 4, 16 -> 8, 32+ -> 16)
val cores = Runtime.getRuntime().availableProcessors()
gradle.startParameter.maxWorkerCount = maxOf(1, minOf(cores / 2, 16))

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

include(
    ":plugin",
    ":nms:api",
    ":nms:v26_1_2",
)

val isCiServer = System.getenv().containsKey("CI")
// Cache build artifacts, so expensive operations do not need to be re-computed
buildCache {
   local {
       isEnabled = !isCiServer
   }
}
