dependencies {
    implementation(project(":nms:api"))
    api(project(":nms:v1_21"))
    // 1.21.2 dev bundle is not published; 1.21.3 has the same NMS surface (8-arg Entry, listOrder).
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
}
