///
/// Define Java Library conventions for Telestion Core projects.
///

plugins {
    id("telestion-java")
    id("java-library")
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        // create one publication for all java components in the project
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://telestion.wuespace.de/")

                // fix shitty gradle api
                val pom = this
                afterEvaluate {
                    pom.description.set(project.description)
                }

                // set the project wide license
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/mit-license.php")
                    }
                }

                // add developer information
                developers {
                    developer {
                        id.set("jvpichowski")
                        name.set("Jan von Pichovski")
                        email.set("janvonpichowski@gmail.com")
                        url.set("https://github.com/jvpichowski")
                        organization.set("WueSpace e. V.")
                        organizationUrl.set("https://www.wuespace.de/")
                    }

                    developer {
                        id.set("cb0s")
                        name.set("Cedric Bös")
                        email.set("cedric.boes@online.de")
                        url.set("https://github.com/cb0s")
                        organization.set("WueSpace e. V.")
                        organizationUrl.set("https://www.wuespace.de/")
                    }

                    developer {
                        id.set("fussel178")
                        name.set("Ludwig Richter")
                        email.set("riluzm@posteo.de")
                        url.set("https://github.com/fussel178")
                        organization.set("WueSpace e. V.")
                        organizationUrl.set("https://www.wuespace.de/")
                    }

                    developer {
                        id.set("pklaschka")
                        name.set("Pablo Klaschka")
                        email.set("contact@pabloklaschka.de")
                        url.set("https://github.com/pklaschka")
                        organization.set("WueSpace e. V.")
                        organizationUrl.set("https://www.wuespace.de/")
                    }
                }

                // add source control management information
                scm {
                    connection.set("scm:git:git://github.com/wuespace/telestion-core.git")
                    developerConnection.set("scm:git:ssh://git@github.com:wuespace/telestion-core.git")
                    url.set("https://github.com/wuespace/telestion-core/tree/main")
                }
            }
        }
    }

    repositories {
        // local repository in build/repo
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repo"))
        }

        // Maven Central
        // TODO: Add Ant task to automatically move the publication from staging to release
        // TODO: To reduce maintainence overhead
        maven {
            name = "mavenCentralStaging"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }

        // GitHub Packages (require Access Token)
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/wuespace/telestion-core")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// Maven Central require signed publications.
// We receive:
//   - the id of the signing key
//   - the WueSpace gpg signing key (actually a sub key)
//   - the password for the signing key
// from the runner instance through previously configured project secrets.
signing {
    // skip signing if not in a CI environment and if nothing is going to be published
    setRequired({ System.getenv("CI") == "true" && gradle.taskGraph.hasTask("publish") })
    // receive gpg parts
    val signingKeyId = findProperty("signingKeyId") as String?
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    // decrypt key in memory
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    // sign publications
    sign(publishing.publications["mavenJava"])
}
