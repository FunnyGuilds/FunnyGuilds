dependencies {
    // api is provided by the plugin jar at runtime, so keep it off this module's shadow output.
    compileOnly(project(":nms:api"))
    paperweight.paperDevBundle("26.1.2.build.72-stable")
}
