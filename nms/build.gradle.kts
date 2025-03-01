import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.userdev.PaperweightUserExtension
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration.Companion.archivesName

allprojects {
    tasks.withType<Javadoc> {
        enabled = false
    }
}

subprojects {
    val mcVersion = MCVersion.matchVersion(project.name)
    if (mcVersion == null || mcVersion.isLegacy()) {
        beforeEvaluate {
            tasks.withType<JavaCompile>().configureEach {
                options.release = 8
            }
        }
        return@subprojects
    }
    
    java {
        toolchain.languageVersion = JavaLanguageVersion.of(mcVersion.javaVersion)
    }
    
    apply(plugin = "io.papermc.paperweight.userdev")

    beforeEvaluate {
        tasks.withType<RemapJar> {
            outputJar = archivesName(project).flatMap { layout.buildDirectory.file("libs/$it-${project.version}.jar") }
        }

        project.the<PaperweightUserExtension>().apply {
            reobfArtifactConfiguration = if (mcVersion.useMojangMappings()) {
                ReobfArtifactConfiguration.MOJANG_PRODUCTION
            }
            else {
                ReobfArtifactConfiguration.REOBF_PRODUCTION
            }
        }
    }
}

data class MCVersion(
    internal val minor: Int,
    internal val patch: Int
) {

    internal val javaVersion = when {
        useMojangMappings() -> 21
        !isLegacy() -> 17
        else -> 8
    }

    /**
     * @return true if the version is 1.20.5 or higher
     */
    internal fun useMojangMappings(): Boolean = minor >= 21 || minor == 20 && patch >= 5

    /**
     * @return true if the version is 1.16 or lower
     */
    internal fun isLegacy(): Boolean = minor <= 16

    companion object {

        fun matchVersion(projectName: String): MCVersion? =
            projectName.split("_").getOrNull(1)?.split("R")?.map(String::toInt)?.let { parts ->
                val minorVersion = parts[0] // 20R3 -> 20
                val patchVersion = parts[1] // 20R3 -> 3
                MCVersion(
                    minorVersion,
                    patchVersion
                )
            }

    }

}
