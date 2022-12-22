import java.text.SimpleDateFormat
import java.util.Date
import de.wuespace.telestion.plugin.TelestionExtension;

///
/// Define Java conventions for Telestion Core projects.
///

// apply extension to get project specific information
val telestion = project.extensions.create("telestion", TelestionExtension::class.java)

plugins {
    id("java")
    // TODO: Add checkstyle configuration and enable and load configuration
    //id("checkstyle")
}

group = "de.wuespace.telestion"
version = file("$rootDir/version.txt").readText().trim()
description = "N/A"

// force project description to be the same like telestion description
afterEvaluate {
    description = telestion.description.getOrElse("N/A")
}

java {
    toolchain {
        // use Java 17 as build and test version
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    // include javadoc and sources in publications
    withJavadocJar();
    withSourcesJar();
}

tasks.withType<Javadoc> {
    // enable HTML5 support in javadoc
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
}

tasks.withType<Jar> {
    metaInf {
        // include project license in publication jars
        from("$rootDir/LICENSE")
    }

    manifest {
        attributes["Build-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date())
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
        attributes["Build-Jdk"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
        attributes["Build-Jdk-Spec"] = System.getProperty("java.version")
        attributes["Build-OS"] = "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}"
        attributes["Specification-Title"] = telestion.prettyName
        attributes["Specification-Version"] = project.version
        attributes["Specification-Vendor"] = "WueSpace e.V."
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Implementation-Vendor"] = project.group
        attributes["X-Compile-Target-JDK"] = java.toolchain.languageVersion
        attributes["X-Compile-Source-JDK"] = java.toolchain.languageVersion
        attributes["Description"] = telestion.description
    }
}

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
    mavenCentral()
}

// TODO: Remove explicit definitions of test libraries once https://github.com/gradle/gradle/issues/15383 is fixed
//dependencies {
//    testImplementation(libs.junit.jupiter.api)
//    testImplementation(libs.hamcrest.core)
//    testImplementation(libs.hamcrest.library)
//    testImplementation(libs.mockito.core)
//    testImplementation(libs.vertx.junit5)
//    testRuntimeOnly(libs.junit.jupiter.engine)
//}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
