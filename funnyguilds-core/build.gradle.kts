plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    api(project(":funnyguilds-server-api"))

    val expressible = "1.3.5"
    implementation("org.panda-lang:expressible:$expressible") // Core library
    implementation("org.panda-lang:expressible-kt:$expressible") // Kotlin extensions
    testImplementation("org.panda-lang:expressible-junit:$expressible") // JUnit extensions

    val sqiffy = "1.0.0-alpha.55"
    ksp("com.dzikoysk.sqiffy:sqiffy-symbol-processor:$sqiffy")
    api("com.dzikoysk.sqiffy:sqiffy:$sqiffy")
}

sourceSets.configureEach {
    kotlin.srcDir("${layout.buildDirectory.get()}/generated/ksp/$name/kotlin/")
}
