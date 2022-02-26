plugins {
    `maven-publish`
}

tasks {
    publishing {
        publications {
            create<MavenPublication>("Clubs") {
                groupId = "dev.gaabriel.clubs"
                artifactId = project.name
                version = Dependencies.ProjectVersion
                from(components["kotlin"])

                pom {
                    name.set("Clubs")
                    description.set("A flexible and powerful command framework with default Guilded API implementations using deck. ")
                }
            }
        }
    }
}