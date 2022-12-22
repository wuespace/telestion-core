plugins {
    id("telestion-java-library")
}

telestion {
    prettyName.set("Telestion Examples")
    description.set("Examples describing different use cases of Telestion")
}

dependencies {
    api(project(":telestion-api"))
    implementation(project(":telestion-services"))

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.mockito.core)
    testImplementation(libs.vertx.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
