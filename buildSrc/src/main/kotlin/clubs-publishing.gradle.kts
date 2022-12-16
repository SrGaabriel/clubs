plugins {
    `maven-publish`
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("Clubs")
            description.set("A flexible and powerful command framework with default Guilded API implementations using deck.")
            url.set(Library.Url)

            developers {
                developer {
                    name.set("SrGaabriel")
                    email.set("srgaabriel@protonmail.com")
                }
            }

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/mit-license.php")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/SrGaabriel/clubs.git")
                developerConnection.set("scm:git:ssh://github.com:SrGaabriel/clubs.git")
                url.set("https://github.com/SrGaabriel/clubs")
            }
        }

        repositories {
            maven(Library.ReleasesRepository) {
                credentials {
                    username = System.getenv("NEXUS_USER")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}