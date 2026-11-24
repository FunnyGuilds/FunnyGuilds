dependencies {
    implementation(project(":extensions:api"))
    implementation(project(":extensions:gui"))
    implementation("eu.okaeri:okaeri-configs-serdes-commons:6.0.0-beta.27")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.12.0")
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.dzikoysk:funnycommands:0.8.0")
    testImplementation("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}
