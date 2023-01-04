plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    // so that external plugins can be resolved in dependencies section
    gradlePluginPortal()
}
