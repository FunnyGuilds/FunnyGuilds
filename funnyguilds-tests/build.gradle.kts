dependencies {
    testImplementation(project(":funnyguilds-core"))
    testImplementation(project(":funnyguilds-targets:fake-server"))

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("ch.qos.logback:logback-classic:1.4.5")

    val junit = "5.8.2"
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
}