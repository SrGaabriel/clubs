plugins {
    kotlin("multiplatform")
    `maven-publish`
    `clubs-publishing`
    signing
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    explicitApi()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api(libs.cache4k)
                api(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.mockk)
            }
        }
    }
}

tasks {
    publishing {
        val docsJar by registering(Jar::class) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Javadocs"
            archiveExtension.set("javadoc")
            archiveClassifier.set("javadoc")
        }

        publications {
            publications.withType<MavenPublication> {
                groupId = Library.Group
                version = Library.Version

                artifact(docsJar.get())
            }
        }
    }
}

signing {
    sign(publishing.publications)
}