import org.gradle.jvm.toolchain.JavaLanguageVersion

dependencies {
    implementation(project(":nms:api"))
    api(project(":nms:v1_21_9"))
    paperweight.paperDevBundle("26.2.build.+")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}