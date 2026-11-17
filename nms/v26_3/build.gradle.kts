import org.gradle.jvm.toolchain.JavaLanguageVersion

dependencies {
    implementation(project(":nms:api"))
    api(project(":nms:v26_1_1"))
    paperweight.paperDevBundle("26.3.build.+")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}