plugins {
    id("telestion-java-library")
}

// some meta information (important for packaging metadata)
telestion {
    prettyName.set("Telestion API")
    description.set("The essential components for a Telestion Project Application")
}

tasks.withType<Test>().configureEach {
    systemProperty("junit.jupiter.displayname.generator.default", "de.wuespace.telestion.api.CamelCase")
}

dependencies {
    api(libs.jackson.core)
    api(libs.jackson.databind)
    api(libs.jackson.annotations)
    api(libs.slf4j.api)
    api(libs.logback.classic)
    api(libs.logstash.logback.encoder)
    api(libs.vertx.core)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.mockito.core)
    testImplementation(libs.vertx.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
