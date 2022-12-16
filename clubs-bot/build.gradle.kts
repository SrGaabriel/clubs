plugins {
    kotlin("jvm")
    `maven-publish`
    `clubs-publishing`
    signing
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":clubs-common"))

    implementation(libs.deck.core)
}

tasks {
    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val docsJar by registering(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Javadocs"
        archiveExtension.set("javadoc")
        from(javadoc)
        dependsOn(javadoc)
    }

    publishing {
        publications {
            create<MavenPublication>("Clubs-Bot") {
                groupId = Library.Group
                version = Library.Version

                from(project.components["java"])
                artifact(sourcesJar.get())
                artifact(docsJar.get())
            }
        }
    }
}

signing {
    sign(publishing.publications["Clubs-Bot"])
}