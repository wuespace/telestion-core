plugins {
    id("telestion-java-library")
}

telestion {
    prettyName.set("Telestion Services")
    description.set("Services and service components that are re-usable in any Telestion Project Application")
}

dependencies {
    api(project(":telestion-api"))
    implementation(libs.vertx.web)
    implementation(libs.vertx.circuitbreaker)
    implementation(libs.jserialcomm)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.mockito.core)
    testImplementation(libs.vertx.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
