plugins {
    id("telestion-java-library")
}

telestion {
    prettyName.set("Telestion Application")
    description.set("The starting point of a Telestion Project Application")
}

dependencies {
    api(project(":telestion-api"))
    implementation(libs.vertx.config)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.mockito.core)
    testImplementation(libs.vertx.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
